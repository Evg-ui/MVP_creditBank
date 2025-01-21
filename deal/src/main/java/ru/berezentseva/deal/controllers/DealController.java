package ru.berezentseva.deal.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.RestClient;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.services.DealProducerService;
import ru.berezentseva.deal.services.DealService;
import ru.berezentseva.deal.exception.StatementException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Контроллер для сделки",
        description = "Принимается заявка от потенциального заемщика для расчета возможных условий кредита")
@RestController
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;
    private final DealProducerService dealProducerService;
    private final RestClient restClient;

    @Autowired
    public DealController(DealService dealService, DealProducerService dealProducerService, RestClient restClient)
    {   this.dealService = dealService;
        this.dealProducerService = dealProducerService;
        this.restClient = restClient;
    }

    @Operation(
            summary = "Расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>",
            description = "На основании запроса на кредит LoanStatementRequestDto"+
                    "Создаются и сохраняются в БД 2 сущности: Client и Statement с ранее сохраненным UUID клиента. "+
                    "Направляется POST на /calculator/offers с присвоением каждому полученному элементу StatementID. "+
                    "Результат - 4 предложения в сортировке от худшего к лучшему"
    )

    // расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>
    @PostMapping("/statement")
    public ResponseEntity<?>  calculateLoanFourOffers(@RequestBody LoanStatementRequestDto request) {
        log.info("Received request into dealController: {}", request.toString());
             try {
        log.info("Creating client and statement");
        List<LoanOfferDto> offers = dealService.createNewApplicationAndClient(request);
        log.info("Client and statement are created");
        return new ResponseEntity<>(offers, HttpStatus.OK);
                        } catch (RestClientException | IllegalArgumentException e) {
            log.error("Ошибка получения предложений. {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @Operation(
            summary = "Выбор одного из предложений. Request - LoanOfferDto, response - void + сохранение данных в БД",
            description = "На основании пришедших по LoanOfferDto "+
                    "достаётся из БД заявка(Statement) по statementId из LoanOfferDto. "+
                    "В заявке обновляется статус, история статусов(List<StatementStatusHistoryDto>), " +
                    "принятое предложение LoanOfferDto устанавливается в поле appliedOffer. "+
                    "Заявка сохраняется."
    )
    @PostMapping("/offer/select")
    public void selectOffer(@RequestBody LoanOfferDto offerDto) throws StatementException {
        UUID statementId;
        try {
            // получаем UUID заявки
            statementId = offerDto.getStatementId();
            dealService.selectOffer(offerDto);
            log.info("Отправка сообщения в Dossier для завершения регистрации.");
            dealProducerService.sendToDossierWithKafka(statementId, "finish-registration", "");
            log.info("Отправка в Dossier для завершения регистрации завершена!");
        } catch (StatementException | IllegalArgumentException e) {
            log.info("Ошибка получения данных о заявке!");
            throw e;
        }
    }

    @Operation(
            summary = "Завершение регистрации + полный подсчёт кредита. Request - FinishRegistrationRequestDto, param - String," +
                    " response void.",
            description = "На основании данных из финальной заявки по пришедшему Id заявки" +
                    "осуществляется наполнение данных для скоринга scoringDto и отправка запроса в калькулятор" +
                    "на основании полученных в ответ данных создается сущность Credit, которая " +
                    "связывается с заявкой и сохраняется в БД, история заявки наполняется новыми этапом"
            
    )

    @PostMapping("/calculate/{statementId}")
    public void calculateCredit(@PathVariable UUID statementId, @RequestBody FinishRegistrationRequestDto request) throws StatementException {
        try {
            log.info("Received request into dealController: {} with statementId {} ", request.toString(), statementId);
            dealService.finishRegistration(statementId, request);
            log.info("Отправка сообщения в Dossier для получения документов от клиента.");
            dealProducerService.sendToDossierWithKafka(statementId, "create-documents", "");
            log.info("Отправка в Dossier для получения документов от клиента завершена!");
        }  catch (RestClientException | IllegalArgumentException e) {
            String errorMessageText = e.getMessage();
            log.error("Ошибка формирования кредита. {}", errorMessageText);
            log.info("Отправка сообщения в Dossier по отказанной заявке.");
            dealProducerService.sendToDossierWithKafka(statementId, "statement-denied", errorMessageText);
            dealService.updateStatusFieldStatement(statementId, ApplicationStatus.CC_DENIED);
            throw e;
    }
    }

    // АПИ для работы с Kafka
// todo добавить описание полей, которые обновятся
    @Operation(
            summary = "Запрос на отправку документов клиентом",
            description = "На основании заявки на кредит statementId "
    )
    //отправка через kafka
    @PostMapping("/document/{statementId}/send")
    public void sendDocuments(@PathVariable UUID statementId) throws StatementException {
        try {
            // обновить статус заявки на prepare docs
            dealService.updateStatusFieldStatement(statementId, ApplicationStatus.PREPARE_DOCUMENTS);
            dealProducerService.sendToDossierWithKafka(statementId, "send-documents", "");
            // затем обновить статус заявки на create docs
            dealService.updateStatusFieldStatement(statementId, ApplicationStatus.DOCUMENT_CREATED);
        } catch (RestClientException | IllegalArgumentException e) {
            log.error("Ошибка отправления запроса на отправку документов в Dossier. {}", e.getMessage());
            throw e;
    }
    }
// todo исправить описание апи
    @Operation(
            summary = "Запрос на отправку документов клиентом",
            description = "На основании заявки на кредит statementId "
    )
    @PostMapping("/document/{statementId}/sign")
    public void signDocuments(@PathVariable UUID statementId) throws StatementException {
        try {
            // обновить поле заявки ses code - где и как
        dealProducerService.sendToDossierWithKafka(statementId, "send-ses", "");
         dealService.updateSesCodeFieldStatement(statementId);
        } catch (RestClientException | IllegalArgumentException e) {
            log.error("Ошибка отправления запроса с ses кодом в Dossier. {}", e.getMessage());
            throw e;
        }
    }

    // todo исправить описание апи
    @Operation(
            summary = "Запрос на отправку документов клиентом",
            description = "На основании заявки на кредит statementId "
    )
        @PostMapping("/document/{statementId}/code")
    public void codeDocuments(@PathVariable UUID statementId)  throws StatementException {
        try {
            // обновить статус заявки на docs signed
            dealService.updateStatusFieldStatement(statementId, ApplicationStatus.DOCUMENT_SIGNED);
            // обновить статус заявки на credit issued
            dealService.updateStatusFieldStatement(statementId, ApplicationStatus.CREDIT_ISSUED);
            // обновить дату подписания заявки
            dealService.updateSignDateFieldStatement(statementId);
            dealProducerService.sendToDossierWithKafka(statementId, "credit-issued", "");
            dealService.updateCreditStatusFieldCredit(statementId, CreditStatus.ISSUED);
        } catch (RestClientException | IllegalArgumentException e) {
            log.error("Ошибка отправления запроса на получение документов в Dossier. {}", e.getMessage());
            throw e;
        }
    }

    // админские АПИ
    @GetMapping("/admin/statement/{statementId}")
    public Statement getStatementById(@PathVariable UUID statementId) {
        return restClient.getStatementById(statementId);
    }

    @GetMapping("/admin/statement")
    public List<Statement> getAllStatements() {
        return restClient.getAllStatements();
    }


}
