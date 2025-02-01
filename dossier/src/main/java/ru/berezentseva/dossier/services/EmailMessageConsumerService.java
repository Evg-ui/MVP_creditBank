package ru.berezentseva.dossier.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.berezentseva.dossier.DTO.EmailMessage;


@Slf4j
@Service

// класс для обработки сообщений из Kafka
public class EmailMessageConsumerService  {

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    private JavaMailSender mailSender;

    private final KafkaTemplate kafkaTemplate;
    // consumer - будет обрабатывать пришедшие события по топикам
    public EmailMessageConsumerService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener
           (topics =
                    {
                    "#{T(ru.berezentseva.sharedconfigs.Enums.KafkaTopics).finishRegistration.getTopic()}",
                    "#{T(ru.berezentseva.sharedconfigs.Enums.KafkaTopics).createDocuments.getTopic()}",
                    "#{T(ru.berezentseva.sharedconfigs.Enums.KafkaTopics).sendDocuments.getTopic()}",
                    "#{T(ru.berezentseva.sharedconfigs.Enums.KafkaTopics).sendSes.getTopic()}",
                    "#{T(ru.berezentseva.sharedconfigs.Enums.KafkaTopics).creditIssued.getTopic()}",
                    "#{T(ru.berezentseva.sharedconfigs.Enums.KafkaTopics).statementDenied.getTopic()}"

//            "finish-registration",
//            "create-documents",
//            "send-documents",
//            "send-ses",
//            "credit-issued",
//            "statement-denied"
    },
     groupId = "dossier-group")

  //  @KafkaListener(topics = "#{T(ru.berezentseva.dossier.services.EmailMessageConsumerService).getTopics()}", groupId = "dossier-group")
    public void sendEmail(EmailMessage emailMessage){
        log.info("Получено сообщение из Kafka: {}. Тема сообщения: {}", emailMessage, emailMessage.getTheme());

        log.info("Отправка сообщения...");
        try {
            log.info("Sending email: from={}, to={}, subject={}, text={}",
                    mailFrom, emailMessage.getAddress(), emailMessage.getTheme(), emailMessage.getText());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(emailMessage.getAddress());
            message.setSubject(String.valueOf(emailMessage.getTheme()));
            message.setText(emailMessage.getText());
            log.info("Сообщение: {}", message);
            mailSender.send(message);
            log.info("Сообщение отправлено успешно!");
        } catch (MailException e) {
            log.error("Ошибка отправки email: {}", e.getMessage(), e);
        }
    }
    }

