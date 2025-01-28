package ru.berezentseva.statement.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.statement.StatementService;
import ru.berezentseva.statement.exception.StatementException;

import java.util.List;

@Slf4j
@Tag(name = "Контроллер для заявки",
        description = "Принимается заявка от потенциального заемщика для расчета возможных условий кредита. " +
                "Здесь осуществляется прескоринг, предлагается список предложений и валидация подходящего предложения клиентом.")
@RestController
@RequestMapping("/statement")
public class StatementController {
    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }


    @Operation(
            summary = "Прескоринг и расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>",
            description = "На основании запроса на кредит LoanStatementRequestDto "+
                    "происходит прескоринг клиента, затем направляется запрос на /deal/statement для " +
                    "получения 4 предложения для клиента в сортировке от худшего к лучшему" +
                    "(от наибольшей ставки к наименьшей)"
    )

    // расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>
    @PostMapping
    public ResponseEntity<?> returnOffersAfterPrescoring(@RequestBody LoanStatementRequestDto request) {

        log.info("Received request into statementController: {}", request.toString());

        try {
            List<LoanOfferDto> offers = statementService.returnOffersAfterPrescoring(request);
            return new ResponseEntity<>(offers, HttpStatus.OK);
        } catch (ScoreException | RestClientException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());}
    }

    @Operation(
            summary = "Выбор одного из предложений. Request - LoanOfferDto, response - void",
            description = "На основании пришедших по LoanOfferDto "+
                    "отправляется запрос на /deal/offer/select для валидации подходящего предложения."
    )
    @PostMapping("/offer")
    public void selectOffer(@RequestBody LoanOfferDto offerDto) throws StatementException {
        try {
            statementService.selectOfferFromDeal(offerDto);
        } catch (RestClientException | IllegalArgumentException e) {
            log.info("Ошибка получения данных о заявке!");
            throw e;
        }
    }


}