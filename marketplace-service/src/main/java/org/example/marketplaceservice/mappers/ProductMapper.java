package org.example.marketplaceservice.mappers;

import org.example.marketplaceservice.dto.ProductDTO;
import org.example.marketplaceservice.models.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ModelMapper mapper;

    @Autowired
    public ProductMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Product convertToProduct(ProductDTO productDTO) {
        return mapper.map(productDTO, Product.class);
    }

    public ProductDTO convertToProductDTO(Product product) {
        return mapper.map(product, ProductDTO.class);
    }
}
