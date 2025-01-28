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
import ru.berezentseva.sharedconfigs.Enums.KafkaTopics;

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

    public void sendToDossierWithKafka(UUID statementId, KafkaTopics topicTheme, String errorMessageText) throws StatementException {
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
        sendEmailToDossier(topicTheme.getTopic(), emailMessage);
        log.info("Сообщение к отправке: {}", emailMessage);
        log.info("Отправка в Dossier завершена");
    }

    // определяем текст для отправки сообщения
    public String changeMessageText(KafkaTopics topicTheme, UUID statementId) {
        String messageText;
        String baseUrl = "http://localhost:8081/deal/document";
        URI uri;

        switch (topicTheme) {
            case KafkaTopics.finishRegistration:  // для получения от клиента полных данных на скоринг
                messageText =  String.format("Ваша заявка %s предварительно одобрена, завершите оформление. ", statementId);
                break;
            case KafkaTopics.createDocuments:
                uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .path("/" + statementId)
                        .path("/send")
                        .build()
                        .toUri();
                messageText =  String.format("Заявка  %s одобрена. \nТребуется сформировать документы. ", statementId) +
                        "\nСформируйте документы по ссылке " + uri;
                break;
            case KafkaTopics.sendDocuments:
                uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .path("/" + statementId)
                        .path("/sign")
                        .build()
                        .toUri();
                messageText = String.format("Документы по заявке %s. \nПодтвердите по ссылке согласие с условиями ", statementId) + uri;
                break;
            case KafkaTopics.sendSes:
                uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .path("/" + statementId)
                        .path("/code")
                        .build()
                        .toUri();
                messageText = "Завалидируйте код из письма. " + uri;
                break;
            case KafkaTopics.creditIssued:
                messageText = "Кредит выдан";
                break;
            case KafkaTopics.statementDenied:
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
    public Theme changeMailTheme(KafkaTopics topicTheme) {
        Theme mailTheme;

        switch (topicTheme) {
            case KafkaTopics.finishRegistration:
                mailTheme = Theme.finishregistration;
                break;
            case KafkaTopics.createDocuments:
                mailTheme = Theme.createdocuments;
                break;
            case KafkaTopics.sendDocuments:
                mailTheme = Theme.senddocuments;
                break;
            case KafkaTopics.sendSes:
                mailTheme = Theme.sendses;
                break;
            case KafkaTopics.creditIssued:
                mailTheme = Theme.creditissued;
                break;
            case KafkaTopics.statementDenied:
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
