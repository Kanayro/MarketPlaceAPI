package org.example.marketplaceservice.util;

import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//Сервис Producer для отправления сообщений в Kafka
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, OrderMessageDTO> template;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    public KafkaProducer(KafkaTemplate<String, OrderMessageDTO> template) {
        this.template = template;
    }

    //Отпровляет сообщение в order-service
    public void sendMessage(OrderMessageDTO message) {
        template.send("order", message);
        logger.info("Message sent");
    }
}
