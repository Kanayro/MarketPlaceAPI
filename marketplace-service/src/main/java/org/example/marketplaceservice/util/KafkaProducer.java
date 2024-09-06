package org.example.marketplaceservice.util;

import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, OrderMessageDTO> template;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, OrderMessageDTO> template) {
        this.template = template;
    }

    public void sendMessage(OrderMessageDTO message) {
        template.send("order", message);
    }
}
