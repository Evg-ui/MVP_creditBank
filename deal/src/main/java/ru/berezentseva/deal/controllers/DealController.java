package ru.berezentseva.deal.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.DealService;
import ru.berezentseva.deal.repositories.ClientRepository;
import ru.berezentseva.deal.repositories.StatementRepository;

import java.io.IOException;
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

    @Autowired     // автоматическое внедрение зависимостей в компоненты приложения
    private ClientRepository clientRepo;
    private StatementRepository statementRepo;

    @Operation(
            summary = "Расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>",
            description = "На основании запроса на кредит LoanStatementRequestDto"+
                    "Создаются и сохраняются в БД 2 сущности: Client и Statement с ранее сохраненным UUID клиента. "+
                    "Направляется POST на /calculator/offers с присвоением каждому полученному элементу StatementID. "+
                    "Результат - 4 предложения в сортировке от худшего к лучшему"
    )

    // расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>
    @PostMapping("/statement")
    public ResponseEntity<?>  calculateLoan(@RequestBody LoanStatementRequestDto request) {
        log.info("Received request into dealController: {}", request);
        //     try {
        log.info("Creating client and statement");
     //   CalculatorService calculatorService = new CalculatorService();
        List<LoanOfferDto> offers = dealService.createApplication(request);
        //result = dealService.createApplication(request);
        log.info("Client and statement are created");
        return new ResponseEntity<>(offers, HttpStatus.OK);

//        } catch (ScoreException | IllegalArgumentException e) {
//            log.error("Ошибка получения заявки. ", e.getMessage());
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(e.getMessage());
//        }
    }

    @PostMapping("/offer/select")
    public void selectOffer(@RequestBody LoanOfferDto offerDto) throws IOException {
        dealService.selectOffer(offerDto);
    }

    @PostMapping("/calculate/{statementId}")
    public void calculate(@PathVariable UUID statementId, @RequestBody FinishRegistrationRequestDto request) throws IOException {
        dealService.finishRegistration(statementId, request);
    }

}
