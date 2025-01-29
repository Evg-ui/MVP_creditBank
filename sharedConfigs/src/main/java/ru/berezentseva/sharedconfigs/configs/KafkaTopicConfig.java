package ru.berezentseva.sharedconfigs.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import ru.berezentseva.sharedconfigs.Enums.KafkaTopics;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Autowired
    private KafkaAdmin kafkaAdmin;
    @Autowired
    private AdminClient adminClient;

    @Bean
    public CommandLineRunner createTopics() {
        return args ->
        {
            List<String> topics = Arrays.asList(
//                    "finish-registration",
//                    "create-documents",
//                    "send-documents",
//                    "send-ses",
//                    "credit-issued",
//                    "statement-denied"
                    KafkaTopics.finishRegistration.getTopic(),
                    KafkaTopics.createDocuments.getTopic(),
                    KafkaTopics.sendDocuments.getTopic(),
                    KafkaTopics.sendSes.getTopic(),
                    KafkaTopics.creditIssued.getTopic(),
                    KafkaTopics.statementDenied.getTopic()

            );

            log.info("Создаются топики");
            log.info("Проверка существования топиков...");

                Set<String> existingTopics = adminClient.listTopics().names().get(); // Получаем список существующих топиков
                for (String topic : topics) {
                    if (!existingTopics.contains(topic)) { // Проверяем, существует ли топик
                        NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
                        kafkaAdmin.createOrModifyTopics(newTopic);
                        log.info("Создан топик: {}", topic);
                    } else {
                        log.info("Топик {} уже существует", topic);
                    }
                }
                log.info("Проверка топиков завершена!");
        };
    }
}
