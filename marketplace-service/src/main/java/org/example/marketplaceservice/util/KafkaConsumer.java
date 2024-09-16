package org.example.marketplaceservice.util;

import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.example.marketplaceservice.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//Слушатель сообщений Kafka
@Component
public class KafkaConsumer {

    private final OrderService service;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    public KafkaConsumer(OrderService service) {
        this.service = service;
    }

    //При получении сообщения обновляет статус заказа
    @KafkaListener(topics = "resp",groupId = "order_status_consumer",
            containerFactory = "userKafkaListenerContainerFactory")
    public void OrderListener(OrderMessageDTO message) {
        logger.info("A message has arrived");
        service.updateOrderStatus(message.getStatus(), message.getId());
    }
}
