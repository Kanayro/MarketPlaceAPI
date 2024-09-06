package org.example.marketplaceservice.util;

import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, OrderMessageDTO> template;

    @InjectMocks
    private KafkaProducer producer;

    @Test
    public void shouldSendMessage() {
        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();

        producer.sendMessage(orderMessageDTO);

        verify(template, times(1)).send("order", orderMessageDTO);
    }


}
