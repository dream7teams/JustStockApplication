package net.juststock.trading.service.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String notificationsTopic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                @Value("${app.kafka.topics.notifications}") String notificationsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationsTopic = notificationsTopic;
    }

    public void publishNotification(String message) {
        kafkaTemplate.send(notificationsTopic, message);
    }

    public void publish(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }
}

