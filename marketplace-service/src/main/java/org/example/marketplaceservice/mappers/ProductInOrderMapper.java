package org.example.marketplaceservice.mappers;

import org.example.marketplaceservice.dto.ProductInOrderDTO;
import org.example.marketplaceservice.models.ProductInOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//Класс маппер для, того чтобы конвертировать ProductInOrder к ProductInOrderDTO и наоборот
@Component
public class ProductInOrderMapper {

    private final ModelMapper mapper;

    @Autowired
    public ProductInOrderMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public ProductInOrderDTO convertToProductInOrderDTO(ProductInOrder productInOrder) {
        return mapper.map(productInOrder, ProductInOrderDTO.class);
    }
}
