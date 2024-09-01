package org.example.orderservice.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "order",groupId = "order_consumer")
    public void ListenOrder(ConsumerRecord<String, String> record) {
        System.out.println("Message: " + record.value());
    }
}
