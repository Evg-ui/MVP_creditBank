package ru.berezentseva.deal.services;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.StatementRepository;
import ru.berezentseva.dossier.DTO.EmailMessage;
import ru.berezentseva.dossier.DTO.Enums.Theme;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
public class DealProducerService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    private final StatementRepository statementRepository;

    public DealProducerService(KafkaTemplate<String, EmailMessage> kafkaTemplate, StatementRepository statementRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.statementRepository = statementRepository;
    }
        public void sendEmailToDossier(String topicForSend, @NotNull EmailMessage emailMessage){
        log.info("Sending email to: " + emailMessage.getAddress());
        log.info("Отправка запроса в Dossier...");
        kafkaTemplate.send(topicForSend, emailMessage);
        log.info("Отправка запроса в Dossier завершена! Топик: {}", topicForSend);
    }

    public void sendToDossierWithKafka(UUID statementId, String topicTheme, String errorMessageText) throws StatementException {
        // готовимся к отправке через кафку и на почту клиенту
        Statement statement = statementRepository.findStatementByStatementId(statementId).orElseThrow(()
                -> new StatementException("Заявка с указанным ID не найдена: " + statementId));
        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + statement.getClientUuid())).getClientUuid();
        log.info("Найден клиент с UUID: {}", client.getClientUuid());

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(client.getEmail());
        emailMessage.setTheme(changeMailTheme(topicTheme));
        emailMessage.setStatementId(statement.getStatementId());
        emailMessage.setText(changeMessageText(topicTheme, statementId) + " " +
                " " + errorMessageText);
        log.info("Отправка письма для {}, по заявке {}, с темой {} ", emailMessage.getAddress(),
                emailMessage.getStatementId(), emailMessage.getTheme());
        sendEmailToDossier(topicTheme, emailMessage);
        log.info("Сообщение к отправке: {}", emailMessage);
        log.info("Отправка в Dossier завершена");
    }

    // определяем текст для отправки сообщения
    public String changeMessageText(String topicTheme, UUID statementId) {
        String messageText;
        String baseUrl;
        URI uri;

        switch (topicTheme) {
            case "finish-registration":  // для получения от клиента полных данных на скоринг
                messageText =  String.format("Ваша заявка %s предварительно одобрена, завершите оформление. ", statementId);
                break;
            case "create-documents":
                baseUrl = "http://localhost:8081/deal/document"; // Базовый URL без statementId
                uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .path("/" + statementId)
                        .path("/send")
                        .build()
                        .toUri();
                messageText =  String.format("Заявка  %s одобрена. \nТребуется сформировать документы. ", statementId) +
                        "\nСформируйте документы по ссылке " + uri;
                break;
            case "send-documents":
                baseUrl = "http://localhost:8081/deal/document"; // Базовый URL без statementId
                uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .path("/" + statementId)
                        .path("/sign")
                        .build()
                        .toUri();
                messageText = String.format("Документы по заявке %s. \nПодтвердите по ссылке согласие с условиями ", statementId) + uri;
                break;
            case "send-ses":
                baseUrl = "http://localhost:8081/deal/document"; // Базовый URL без statementId
                uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .path("/" + statementId)
                        .path("/code")
                        .build()
                        .toUri();
                messageText = "Завалидируйте код из письма. " + uri;
                break;
            case "credit-issued":
                messageText = "Кредит выдан";
                break;
            case "statement- denied":
                messageText = "Ваша заявка отклонена.";
                break;
            default:
                messageText = "";
                log.error("Топик не заведен!");
                break;
        }
        log.info("Сообщение к отправке: " + messageText);
        return messageText;
    }

    // определяем тему письма для отправки сообщения
    public Theme changeMailTheme(String topicTheme) {
        Theme mailTheme;

        switch (topicTheme) {
            case "finish-registration":
                mailTheme = Theme.finishregistration;
                break;
            case "create-documents":
                mailTheme = Theme.createdocuments;
                break;
            case "send-documents":
                mailTheme = Theme.senddocuments;
                break;
            case "send-ses":
                mailTheme = Theme.sendses;
                break;
            case "credit-issued":
                mailTheme = Theme.creditissued;
                break;
            case "statement-denied":
                mailTheme = Theme.statementdenied;
                break;
            default:
                log.error("Топик не заведен!");
                return null;
        }
        log.info("Тема сообщения: " + mailTheme);
        return mailTheme;
    }
}
