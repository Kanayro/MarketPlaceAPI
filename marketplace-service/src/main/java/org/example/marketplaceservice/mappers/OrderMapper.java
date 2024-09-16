package org.example.marketplaceservice.mappers;

import org.example.marketplaceservice.dto.OrderDTO;
import org.example.marketplaceservice.models.Order;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//Класс маппер для, того чтобы конвертировать Order к OrderDTO и наоборот
@Component
public class OrderMapper {

    private final ModelMapper mapper;

    @Autowired
    public OrderMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public OrderDTO convertToOrderDTO(Order order) {
        return mapper.map(order, OrderDTO.class);
    }

    public Order convertToOrder(OrderDTO orderDTO) {
        return mapper.map(orderDTO, Order.class);
    }
}
