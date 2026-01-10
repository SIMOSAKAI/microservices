package org.code.notificationservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaNotificationConsumer {

    @KafkaListener(topics = "emprunt-created", groupId = "notif")
    public void consume(String message) {
        System.out.println("📢 NOTIFICATION RECEIVED: " + message);
    }
}

