package org.example.marketplaceservice.util;

import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.example.marketplaceservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {

    private final OrderService service;

    @Autowired
    public KafkaConsumer(OrderService service) {
        this.service = service;
    }

    @KafkaListener(topics = "resp",groupId = "order_status_consumer",
            containerFactory = "userKafkaListenerContainerFactory")
    public void OrderListener(OrderMessageDTO message) {
        service.updateOrderStatus(message.getStatus(), message.getId());
    }
}
