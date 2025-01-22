package ru.berezentseva.gateway.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/creditBank") // Базовый путь для API gateway
@Tag(name = "Gateway API", description = "API для взаимодействия с кредитным конвейером")
public class GatewayController {

    private final RestTemplate restTemplate;

    public GatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/statement/{statementId}")
    public ResponseEntity<?> getStatementById(@PathVariable UUID statementId) {
        String dealServiceUrl = "http://localhost:8081/deal/statement/" + statementId; // Используйте имя сервиса
        return restTemplate.getForEntity(dealServiceUrl, LoanStatementRequestDto.class);
    }

    @GetMapping("/statement")
    public ResponseEntity<?> getAllStatements() {
        String dealServiceUrl = "http://localhost:8081/deal/statement"; // Используйте имя сервиса
        ResponseEntity<LoanStatementRequestDto[]> response = restTemplate.getForEntity(dealServiceUrl, LoanStatementRequestDto[].class);
        return ResponseEntity.ok(Arrays.asList(Objects.requireNonNull(response.getBody())));
    }


    @GetMapping("/admin/statement/{statementId}")
    public ResponseEntity<?> getAdminStatementById(@PathVariable UUID statementId) {
        String dealServiceUrl = "http://localhost:8081/deal/admin/statement/" + statementId; // Используйте имя сервиса
        return restTemplate.getForEntity(dealServiceUrl, LoanStatementRequestDto.class);
    }

    @GetMapping("/admin/statement")
    public ResponseEntity<?> getAllAdminStatements() {
        String dealServiceUrl = "http://localhost:8081/deal/admin/statement/"; // Используйте имя сервиса
        ResponseEntity<LoanStatementRequestDto[]> response = restTemplate.getForEntity(dealServiceUrl, LoanStatementRequestDto[].class);
        return ResponseEntity.ok(Arrays.asList(response.getBody()));
    }

}
