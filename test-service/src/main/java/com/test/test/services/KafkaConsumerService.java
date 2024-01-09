package com.test.test.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "test-topic", groupId = "1")
    public void consumeMessage(String message) {
        // Process the received message
        System.out.println("Kafka received message: " + message);
    }
}
