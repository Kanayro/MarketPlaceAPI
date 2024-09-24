package org.example.orderservice.services;

import org.example.orderservice.dto.OrderMessageDTO;
import org.example.orderservice.models.ProcessedMessage;
import org.example.orderservice.repositories.ProcessedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//Слушатель сообщений Kafka
@Component
public class KafkaConsumer {

    private final KafkaProducer producer;
    private final ProcessedMessageRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    public KafkaConsumer(KafkaProducer producer, ProcessedMessageRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }

    //При получении сообщения симулирует обработку заказа
    @KafkaListener(topics = "order",groupId = "order_consumer",
            containerFactory = "userKafkaListenerContainerFactory")
    public void ListenOrder(OrderMessageDTO message) {
        logger.info("A message has arrived");
        for (ProcessedMessage messages : repository.findAll()) {
            System.out.println(messages.getId());
        }
        if(repository.findById(message.getId()).isPresent()) {
            logger.warn("A message is duplicate");
            return;
        }
        orderIsSend(10, message.getId());
        orderIsReady(20,message.getId());
        repository.save(new ProcessedMessage(message.getId()));
    }

    public void orderIsSend(long delay, int id) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> {
            producer.sendOrderStatus(new OrderMessageDTO(id,"SENT"));
            service.shutdown();
        },delay, TimeUnit.SECONDS);
        logger.info("Status changed to SENT");
    }

    public void orderIsReady(long delay,int id) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> {
            producer.sendOrderStatus(new OrderMessageDTO(id,"READY"));
            service.shutdown();
        },delay, TimeUnit.SECONDS);
        logger.info("Status changed to READY");
    }
}
