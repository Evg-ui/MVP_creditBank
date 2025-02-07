package ru.berezentseva.deal.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.ChangeType;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.services.DealService;

import java.util.List;
import java.util.UUID;

@Controller
@Tag(name = "Админский контроллер",
        description = "Для просмотра информации по заявкам и ручных изменений.")
@RestController
@RequestMapping("/deal")
public class DealAdminController {
    private static final Logger log = LoggerFactory.getLogger(DealAdminController.class);

    private final DealService dealService;

    public DealAdminController(DealService dealService) {
        this.dealService = dealService;
    }

    // админские АПИ
    @Operation(
            summary = "Получение информации о заявке по её UUID."
    )
    @GetMapping("/admin/statement/{statementId}")
    public ResponseEntity<Statement> getStatementById(@PathVariable UUID statementId) throws StatementException{
        try {
            Statement statement = dealService.getStatementById(statementId);
            return ResponseEntity.ok(statement);
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Ошибка отправления запроса. {}", e.getMessage());
            throw e;
        }
}

    @Operation(
            summary = "Получение информации обо всех заявках"
    )
    @GetMapping("/admin/statement")
    public List<Statement> getAllStatements() {
        try {
        log.info("Получение всех заявок: {}", dealService.getAllStatements());
        return dealService.getAllStatements();
        } catch (RestClientException | IllegalArgumentException e) {
            log.error("Ошибка отправления запроса. {}", e.getMessage());
            throw e;
        }
    }

    @Operation(
            summary = "Обновление статуса заявки."
    )
    @PutMapping("/admin/statement/{statementId}/status")
    public void updateApplicationStatus(@PathVariable UUID statementId) throws StatementException {
        try {
            log.info("Обновление статуса заявки {}", statementId);
            dealService.updateStatusFieldStatement(statementId, ApplicationStatus.DOCUMENT_CREATED, ChangeType.MANUAL);
          //  return ResponseEntity.ok("Status was updated successfully!");
        } catch (StatementException | RestClientException | IllegalArgumentException e) {
            log.error("Ошибка отправления запроса. {}", e.getMessage());
            throw e;
        }
    }
}