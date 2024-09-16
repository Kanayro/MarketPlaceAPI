package org.example.orderservice.services;

import org.example.orderservice.dto.OrderMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//Сервис Producer для отправления сообщений в Kafka
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, OrderMessageDTO> template;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, OrderMessageDTO> template) {
        this.template = template;
    }

    //Отпровляет сообщение в marketplace-service
    public void sendOrderStatus(OrderMessageDTO message) {
        template.send("resp",message);
    }

}
