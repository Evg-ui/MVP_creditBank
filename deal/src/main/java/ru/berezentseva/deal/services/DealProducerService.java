package ru.berezentseva.deal.services;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.berezentseva.dossier.DTO.EmailMessage;

@Slf4j
@Service
public class DealProducerService {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public DealProducerService(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

        public void sendEmailToDossier(String topicForSend, @NotNull EmailMessage emailMessage){
        // Логика отправки письма
        log.info("Sending email to: " + emailMessage.getAddress());
        kafkaTemplate.send(topicForSend, emailMessage);
        //log.info("Отправка в Dossier завершена");
        // Здесь можно использовать Spring Mail для отправки письма
    }
}
