package org.example.marketplaceservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String server;

    // Метод для создания фабрики продюсеров (producers) Kafka.
    public ProducerFactory<String, OrderMessageDTO> producerFactory() {
        Map<String, Object> config = new HashMap<>(); // Создаем новую карту для хранения конфигурации продюсера.
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server); // Указываем адреса серверов Kafka.
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Указываем класс сериализации для ключа.
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Указываем класс сериализации для значения (OrderMessageDTO).

        return new DefaultKafkaProducerFactory<>(config); // Создаем и возвращаем фабрику продюсеров с заданной конфигурацией.
    }

    // Метод для создания фабрики потребителей (consumers) Kafka.
    public ConsumerFactory<String, OrderMessageDTO> consumerFactory() {
        Map<String, Object> config = new HashMap<>(); // Создаем новую карту для хранения конфигурации потребителя.
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server); // Указываем адреса серверов Kafka.
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order_status_consumer"); // Указываем идентификатор группы потребителей.
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10); // Указываем максимальное количество записей, которые будет получать потребитель за один раз.
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Указываем класс десериализации для ключа.
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // Указываем класс десериализации для значения (OrderMessageDTO).
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); // Отключаем использование заголовков для информации о типе.
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.example.marketplaceservice.dto.OrderMessageDTO"); // Указываем класс для десериализации значения.

        return new DefaultKafkaConsumerFactory<>(config); // Создаем и возвращаем фабрику потребителей с заданной конфигурацией.
    }

    // Метод для создания нового топика Kafka.
    @Bean
    public NewTopic newTopic() {
        return new NewTopic("order", 1, (short) 1); // Создаем новый топик с именем "order", 1 разделом и 1 фактором репликации.
    }

    // Метод для создания шаблона KafkaTemplate, который используется для отправки сообщений вKafka.
    @Bean
    public KafkaTemplate<String, OrderMessageDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory()); // Создаем и возвращаем KafkaTemplate с фабрикой продюсеров.
    }

    // Метод для создания фабрики контейнеров для потребителей, который управляет прослушиванием сообщений из Kafka.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderMessageDTO> userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderMessageDTO> factory = new ConcurrentKafkaListenerContainerFactory<>(); // Создаем новую фабрику контейнеров.
        factory.setConsumerFactory(consumerFactory()); // Устанавливаем фабрику потребителей.
        return factory;
    }
}