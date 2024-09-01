package org.example.orderservice.services;

import org.example.orderservice.dto.OrderMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaConsumer {

    private final KafkaProducer producer;

    @Autowired
    public KafkaConsumer(KafkaProducer producer) {
        this.producer = producer;
    }

    @KafkaListener(topics = "order",groupId = "order_consumer",
            containerFactory = "userKafkaListenerContainerFactory")
    public void ListenOrder(OrderMessageDTO message) {
        orderIsSend(10, message.getId());
        orderIsReady(20,message.getId());
    }

    public void orderIsSend(long delay, int id) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> {
            producer.sendOrderStatus(new OrderMessageDTO(id,"SENT"));
            service.shutdown();
        },delay, TimeUnit.SECONDS);

    }

    public void orderIsReady(long delay,int id) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> {
            producer.sendOrderStatus(new OrderMessageDTO(id,"READY"));
            service.shutdown();
        },delay, TimeUnit.SECONDS);
    }
}
