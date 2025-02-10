package ru.berezentseva.dossier.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.berezentseva.dossier.DTO.EmailMessage;

@Service
// класс для обработки сообщений из Kafka
public class EmailMessageConsumerService  {
    private static final Logger log = LoggerFactory.getLogger(EmailMessageConsumerService.class);

   private final ObjectMapper objectMapper;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    private JavaMailSender mailSender;

    public EmailMessageConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//
//    @Autowired
//    private KafkaTemplate<String, EmailMessage> kafkaTemplate;

    @Async
    @KafkaListener
            (topics =
                    {
                            "#{T(ru.berezentseva.dossier.DTO.Enums.KafkaTopics).finishRegistration.getTopic()}",
                            "#{T(ru.berezentseva.dossier.DTO.Enums.KafkaTopics).createDocuments.getTopic()}",
                            "#{T(ru.berezentseva.dossier.DTO.Enums.KafkaTopics).sendDocuments.getTopic()}",
                            "#{T(ru.berezentseva.dossier.DTO.Enums.KafkaTopics).sendSes.getTopic()}",
                            "#{T(ru.berezentseva.dossier.DTO.Enums.KafkaTopics).creditIssued.getTopic()}",
                            "#{T(ru.berezentseva.dossier.DTO.Enums.KafkaTopics).statementDenied.getTopic()}"

//            "finish-registration",
//            "create-documents",
//            "send-documents",
//            "send-ses",
//            "credit-issued",
//            "statement-denied"
                    },
                    groupId = "dossier-group"
                    , containerFactory = "kafkaListenerContainerFactory")

    //  @KafkaListener(topics = "#{T(ru.berezentseva.dossier.services.EmailMessageConsumerService).getTopics()}", groupId = "dossier-group")
    public void sendEmail(String emailMessageJSON) throws JsonProcessingException {
        EmailMessage emailMessage = objectMapper.readValue(emailMessageJSON, EmailMessage.class);

    //    EmailMessage emailMessage = getEmailMessage(email);
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
            log.error("Ошибка отправки email: {}, сообщение: {}", e.getMessage(), emailMessage, e);
        }
    }
}