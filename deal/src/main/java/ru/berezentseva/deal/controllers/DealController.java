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
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.DealService;
import ru.berezentseva.deal.exception.DealException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Контроллер для сделки",
        description = "Принимается заявка от потенциального заемщика для расчета возможных условий кредита")
@RestController
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;

    @Autowired
    public DealController(DealService dealService)
    {        this.dealService = dealService;    }

    @Operation(
            summary = "Расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>",
            description = "На основании запроса на кредит LoanStatementRequestDto "+
                    "Создаются и сохраняются в БД 2 сущности: Client и Statement с ранее сохраненным UUID клиента. "+
                    "Направляется POST на /calculator/offers с присвоением каждому полученному элементу StatementID. "+
                    "Результат - 4 предложения в сортировке от худшего к лучшему"
    )

    // расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>
    @PostMapping("/statement")
    public ResponseEntity<?>  calculateLoan(@RequestBody LoanStatementRequestDto request) {
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
    public void selectOffer(@RequestBody LoanOfferDto offerDto) throws DealException {
        try {
            dealService.selectOffer(offerDto);
        } catch (DealException | IllegalArgumentException e) {
            log.info("Ошибка получения данных о заявке!");
            throw e;
        }
    }

    @Operation(
            summary = "Завершение регистрации + полный подсчёт кредита. Request - FinishRegistrationRequestDto, param - String," +
                    " response void.",
            description = "На основании данных из финальной заявки по пришедшему Id заявки " +
                    "осуществляется наполнение данных для скоринга scoringDto и отправка запроса в калькулятор " +
                    "на основании полученных в ответ данных создается сущность Credit, которая " +
                    "связывается с заявкой и сохраняется в БД, история заявки наполняется новыми этапом"
            
    )
    @PostMapping("/calculate/{statementId}")
    public void calculate(@PathVariable UUID statementId, @RequestBody FinishRegistrationRequestDto request) {
        try {
            log.info("Received request into dealController: {} with statementId {} ", request.toString(), statementId);
            dealService.finishRegistration(statementId, request);
        }  catch (RestClientException | IllegalArgumentException e) {
        log.error("Ошибка формирования кредита. {}", e.getMessage());
            throw e;
    }
    }

}
