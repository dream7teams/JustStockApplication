package net.juststock.trading.service.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true")
public class KafkaListenerService {

    @Value("${app.kafka.topics.notifications}")
    private String notificationsTopic;

    @KafkaListener(topics = "${app.kafka.topics.notifications}", groupId = "${spring.kafka.consumer.group-id}")
    public void onNotification(ConsumerRecord<String, String> record) {
        // Basic log of consumed messages; extend as needed
        System.out.println("[Kafka] Received on " + record.topic() + ": " + record.value());
    }
}
