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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.gateway.GatewayService;

import java.net.URI;
import java.util.UUID;

@Tags
@Slf4j
@RestController
@RequestMapping("/creditBank")
@Tag(name = "Gateway API for Admin", description = "API администратора для взаимодействия с кредитным конвейером")
public class GatewayAdminController {
    private final GatewayService gatewayService;

    public GatewayAdminController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    // администратор - получить заявку по ID
    @PostMapping("/admin/statement/{statementId}")
    @Operation(summary = "Просмотр заявки администратором.",
            description = "Администратор системы может просмотреть заявку по ее statementID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanStatementRequestDto.class))}), // Указываем, что возвращается в случае успеха
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content) // Описание ошибки 500
    })
    public ResponseEntity<?> statementAdminRequest(@Parameter(description = "UUID заявки, по которой требуется получить данные.")
                                                       @PathVariable UUID statementId) {
        String baseUrl = "http://localhost:8081/deal/admin/statement";
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/" + statementId)
                .build()
                .toUri();
        try{
            log.info("Gateway received request: {}", statementId.toString());
            return gatewayService.getAdminResponseEntity(uri.toString(), statementId);
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Error from \"http://localhost:8081/deal/admin/statement\": {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // администратор - получить заявку по ID
    @PostMapping("/admin/statement")
    @Operation(summary = "Просмотр всех  заявок администратором.",
            description = "Администратор системы может просмотреть все существующие заявки."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное завершение.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanStatementRequestDto.class))}), // Указываем, что возвращается в случае успеха
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content), // Описание ошибки 400
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content) // Описание ошибки 500
    })
    public ResponseEntity<?> allStatementsAdminRequest() {
        String baseUrl = "http://localhost:8081/deal/admin/statement";
        try{
            return gatewayService.getAdminResponseEntity(baseUrl);
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Error from \"http://localhost:8081/deal/admin/statement\": {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

}
