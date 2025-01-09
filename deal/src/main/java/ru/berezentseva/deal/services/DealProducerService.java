package ru.berezentseva.deal.services;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.StatementRepository;
import ru.berezentseva.dossier.DTO.EmailMessage;
import ru.berezentseva.dossier.DTO.Enums.Theme;

import java.util.NoSuchElementException;

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
        log.info("Отправка в Dossier...");
        kafkaTemplate.send(topicForSend, emailMessage);
        log.info("Отправка в Dossier завершена! Топик: {}", topicForSend);
    }

    public void sendToDossierWithKafka(LoanOfferDto offerDto) throws StatementException {
        // готовимся к отправке через кафку и на почту клиенту
        Statement statement = statementRepository.findStatementByStatementId(offerDto.getStatementId()).orElseThrow(()
                -> new StatementException("Заявка с указанным ID не найдена: " + offerDto.getStatementId()));
        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + statement.getClientUuid())).getClientUuid();
        log.info("Найден клиент с UUID: {}", client.getClientUuid());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(client.getEmail());
        emailMessage.setTheme(Theme.finishregistration);
        emailMessage.setStatementId(statement.getStatementId());
        emailMessage.setText("finishregistration: Завершите оформление!");
        log.info("Отправка письма для {}, по заявке {}, с темой {} ", emailMessage.getAddress(),
                emailMessage.getStatementId(), emailMessage.getTheme());
        sendEmailToDossier("finish-registration", emailMessage);
        log.info("Сообщение к отправке: {}", emailMessage);
        log.info("Отправка в Dossier завершена");
    }
}
