package org.example.notifyservice.consumer;

import org.example.notifyservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;

public class OrderCreatedConsumer {

    private final EmailService emailService;

    public OrderCreatedConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(groupId = "notification", topics = "order-created")
    public void consume(String email) {
        emailService.sendOrderCreatedEmail(email);
    }
}
