package ru.berezentseva.deal.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.berezentseva.dossier.DTO.EmailMessage;

@Slf4j
@Service
public class DealProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public DealProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

        public void sendEmail(String topicForSend, EmailMessage emailMessage){
        // Логика отправки письма
      //  log.info("Sending email to: " + emailMessage.getAddress());
        kafkaTemplate.send(topicForSend, String.valueOf(emailMessage));
        // Здесь можно использовать Spring Mail для отправки письма
    }
}
