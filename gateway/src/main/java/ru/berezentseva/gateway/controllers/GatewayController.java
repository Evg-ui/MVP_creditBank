package ru.berezentseva.gateway.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.deal.RestClient;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.gateway.GatewayService;
import ru.berezentseva.statement.StatementService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@ComponentScan(basePackages = {"ru.berezentseva.gateway", "ru.berezentseva.deal"})
@RequestMapping("/api/creditBank") // Базовый путь для API gateway
@Tag(name = "Gateway API", description = "API для взаимодействия с кредитным конвейером")
public class GatewayController {

    private final RestClient restClient;
    private final GatewayService gatewayService;

    public GatewayController(RestClient restClient, GatewayService gatewayService) {
        this.restClient = restClient;
        this.gatewayService = gatewayService;
    }

    // вход клиента для создания запроса
    @PostMapping("/statement")
    public ResponseEntity<?>  clientRequest(@RequestBody LoanStatementRequestDto request) {
        log.info("Направляем заявку в МС Заявка(statement): {}", request.toString());

        try {
            List<LoanOfferDto> offers = gatewayService.postClientRequest(request);
            log.info("Заявка обработана сервисом Statement!");
            return new ResponseEntity<>(offers, HttpStatus.OK);
        } catch (ScoreException | RestClientException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());}
    };

    //    // вход  todo перенести в пропертис?
//    private final String DEAL_SERVICE_URL = "http://localhost:8081/";
//
//    @GetMapping("/statement/{statementId}")
//    @Operation(summary = "Получить заявку по ID")
//    public ResponseEntity<Statement> getStatementById(@PathVariable UUID statementId) {
//        Statement statement = restClient.getStatementById(statementId);
//        return ResponseEntity.ok(statement);
//    }
//
//    @GetMapping("/statement")
//    @Operation(summary = "Получить все заявки")
//    public ResponseEntity<List<Statement>> getAllStatements() {
//        List<Statement> statements = restClient.getAllStatements();
//        return ResponseEntity.ok(statements);
//
//    }

}
