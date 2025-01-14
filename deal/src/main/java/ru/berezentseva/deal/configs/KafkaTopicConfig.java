package ru.berezentseva.deal.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    // TODO сделать проверку существования топиков
    @Bean
    public CommandLineRunner createTopics() {
        return args ->
        {
            List<String> topics = Arrays.asList(
                    "finish-registration",
                    "create-documents",
                    "send-documents",
                    "send-ses",
                    "credit-issued",
                    "statement-denied"
            );

            log.info("Создаются топики");
            for (String topic : topics) {
                NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
                kafkaAdmin.createOrModifyTopics(newTopic);
                System.out.println("Created topic: " + topic);
            }
            log.info("Топики созданы!");
        };
    }
}
