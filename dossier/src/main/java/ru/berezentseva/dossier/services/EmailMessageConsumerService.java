package ru.berezentseva.dossier.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.berezentseva.dossier.DTO.EmailMessage;

@Service
@Slf4j
// класс для обработки сообщений из Kafka
public class EmailMessageConsumerService  {
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

    public void sendEmail(String topicForSend, EmailMessage emailMessage){
        // Логика отправки письма
     //   log.info("Sending email to: " + emailMessage.getAddress());
        kafkaTemplate.send(topicForSend, emailMessage);
        // Здесь можно использовать Spring Mail для отправки письма
    }
    public void listen(String message){
        System.out.println("Received message: {}" + message);
        }
    }

