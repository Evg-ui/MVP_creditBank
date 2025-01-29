package ru.berezentseva.gateway.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.gateway.GatewayService;

import java.net.URI;
import java.util.UUID;

@Tags
@Slf4j
@RestController
@RequestMapping("/creditBank")
@Tag(name = "Gateway API", description = "API клиента для взаимодействия с кредитным конвейером")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    // заявка от клиента - LoanStatementRequestDto - возвращает ошибку прескоринга или 4 оффера LoanOfferDto
    @PostMapping("/statement")
    @Operation(summary = "Подача заявки клиентом.",
    description = "Клиент подает заявку на кредит. Происходит прескоринг. " +
            "В случае успешного прескоринга клиенту предлагаются на выбор 4 предложения. " +
            "В базу сохраняется заявка(в Statement) и данные клиента (в Client)." +
            "Принимает DTo LoanStatementRequestDto, возвращает 4 DTo LoanOfferDto либо ошибку прескоринга."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanOfferDto.class))}), // Указываем, что возвращается в случае успеха
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content) // Описание ошибки 500
    })
    public ResponseEntity<?> statementRequest(@RequestBody LoanStatementRequestDto request) {
        try{
        log.info("Gateway received request: {}", request.toString());
        ResponseEntity<?> response
                = gatewayService.getResponseEntity("http://localhost:8082/statement", request);
            return  response;
        } catch (ScoreException | RestClientException | IllegalArgumentException e) {
            log.error("Error from \"http://localhost:8081/deal/statement\": {}", e.getMessage());
           // throw e;
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // клиент что-то выбирает, ответ - пусто, в базе новая запись
    @PostMapping("/statementSelect")
    @Operation(summary = "Выбор клиентом подходящего предложения в базу. ",
            description = "В рамках метода осуществляется выбор и запись выбранного предложения. " +
                    "В базе обновляются данные клиента и заявки, статусы. " +
                    "Отправляется письмо клиенту с запросом на предоставление своих данных " +
                    "для завершения регистрации заявки. " +
                    "Принимает DTo LoanOfferDto, ничего не возвращает, результат - сущности в БД." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение.",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content) // Описание ошибки 500
    })
    public ResponseEntity<?> statementSelect(@RequestBody LoanOfferDto request) throws StatementException {
        try {
            log.info("Gateway received request: {}", request);
            gatewayService.getResponseEntity("http://localhost:8082/statement/offer", request);
            return ResponseEntity.ok("Предложение сформировано!");
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Ошибка при выполнении запроса: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // завершение регистрации заявки и оформление кредита
    @PostMapping("/statement/registration/{statementId}")
    @Operation(summary = "Завершение регистрации и скоринг заявки.",
            description = "Этот метод завершает регистрацию сделки и " +
                    "выполняет скоринг на основе предоставленных данных. В базе создается кредит (в Credit), " +
                    "обновляются данные заявки, клиента. В случае успешного скоринга клиенту направляется письмо " +
                    "с запросом на оформление документов. " +
                    "Принимает UUID заявки и Dto FinishRegistrationRequestDto." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение.",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content) // Описание ошибки 500
    })
    public ResponseEntity<?> dealFinishRegAndScoring(@Parameter(description = "UUID заявки, по которой получены данные для завершения сделки.")
            @PathVariable UUID statementId, @RequestBody FinishRegistrationRequestDto request) {
        String baseUrl = "http://localhost:8081/deal/calculate"; // Базовый URL без statementId
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/" + statementId) // Добавляем statementId к пути
                .build()
                .toUri();
        try {
            log.info("Gateway received request: {}", request);
            return gatewayService.getResponseEntity(uri.toString(), request);
        } catch (RestClientException | IllegalArgumentException | ScoreException e) {
            log.error("Error from \"http://localhost:8081/deal/calculate\": {}", e.getMessage());
            // throw e;
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

// StatementException |
    // запрос на получение документов
    @PostMapping("/document/{statementId}")
    @Operation(summary = "Отправка запроса клиенту на получение документов.",
            description = "Этот метод отправляет письмо - запрос клиенту на получение документов с ссылкой. " +
                    "Принимает UUID заявки." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение формирования документов."),
            @ApiResponse(responseCode = "400", description = "Неверный запрос, неверный UUID заявки."), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера") // Описание ошибки 500
    })
    public ResponseEntity<?> dealSendDocuments(@Parameter(description = "UUID заявки, по которой требуется получить документы.")
                                                     @PathVariable UUID statementId) {
        String baseUrl = "http://localhost:8081/deal/document";
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/" + statementId)
                .path("/send")
                .build()
                .toUri();
        try {
        log.info("Gateway received request: {}", statementId);
        return gatewayService.getResponseEntity(uri.toString(), statementId);
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Error from \"http://localhost:8081/deal/document/send\": {}", e.getMessage());
            // throw e;
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // запрос на подписание документов
    @PostMapping("/document/{statementId}/sign")
    @Operation(summary = "Отправка запроса клиенту на подписание документов.",
            description = "Этот метод отправляет письмо - запрос клиенту на подписание документов с ссылкой. " +
                    "Клиент может отказаться. " +
                    "Принимает UUID заявки." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение подписания документов."),
            @ApiResponse(responseCode = "400", description = "Неверный запрос, неверный UUID заявки."), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера") // Описание ошибки 500
    })
        public ResponseEntity<?> dealSignDocuments(@Parameter(description = "UUID заявки, по которой требуется подписание документов.")
                                               @PathVariable UUID statementId) {
        String baseUrl = "http://localhost:8081/deal/document";
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/" + statementId)
                .path("/sign")
                .build()
                .toUri();
        try {
        log.info("Gateway received request: {}", statementId);
        return gatewayService.getResponseEntity(uri.toString(), statementId);
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Error from \"http://localhost:8081/deal/document/sign\": {}", e.getMessage());
            // throw e;
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // запрос на валидацию кода и завершение оформления кредита
    @PostMapping("/document/{statementId}/sign/code")
    @Operation(summary = "Отправка запроса клиенту на валидацию кода.",
            description = "Этот метод отправляет письмо - запрос клиенту на подтверждение кода валидации. "  +
                    "Клиент может отказаться. " +
                    "Принимает UUID заявки." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная валидация кода."),
            @ApiResponse(responseCode = "400", description = "Неверный запрос, неверный UUID заявки."), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера") // Описание ошибки 500
    })
    public ResponseEntity<?> dealSesCodeDocuments(@Parameter(description = "UUID заявки, по которой требуется валидация ses-кода.")
                                               @PathVariable UUID statementId) {
            String baseUrl = "http://localhost:8081/deal/document"; // Базовый URL без statementId
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/" + statementId)
                    .path("/code")
                    .build()
                    .toUri();
            try{
        log.info("Gateway received request: {}", statementId);
        return gatewayService.getResponseEntity(uri.toString(), statementId);
    } catch (StatementException | RestClientException | IllegalArgumentException e) {
        log.error("Error from \"http://localhost:8081/deal/document/code\": {}", e.getMessage());
        // throw e;
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }


    }
}
