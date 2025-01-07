package ru.berezentseva.dossier.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.berezentseva.dossier.DTO.EmailMessage;

@Service
@Slf4j
// класс для обработки сообщений из Kafka
public class EmailMessageConsumerService  {
    @Autowired
    private JavaMailSender mailSender;

    private final KafkaTemplate kafkaTemplate;

    public EmailMessageConsumerService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = {
            "finish-registration",
            "create-documents",
            "send-documents",
            "send-ses",
            "credit-issued",
            "statement-denied"
    }, groupId = "dossier-group")

//    public void listen(EmailMessage emailMessage){
//        //  sendEmail(emailMessage);
//        log.info("Received message: {}, {}", emailMessage.getText(), emailMessage.getAddress());
//        System.out.println("Received message: {}" + emailMessage.getText() + " " + emailMessage.getAddress());
//    }

    public void sendEmail(EmailMessage emailMessage){
        log.info("Отправка сообщения...");
        try {
            log.info("Sending email to: {}", emailMessage.getAddress());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getAddress());
            message.setSubject(String.valueOf(emailMessage.getTheme()));
            message.setText(emailMessage.getText());
            mailSender.send(message);
            log.info("Сообщение отправлено успешно!");
        } catch (MailException e) {
            log.error("Ошибка отправки email: {}", e.getMessage(), e);
            // Добавьте здесь логику обработки ошибки (например, сохранение сообщения в базу данных для повторной отправки)
        }
    }
    }

