package org.example.marketplaceservice.util;

import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//Сервис для валидации продуктов при добавлении
@Component
public class ProductValidator implements Validator {

    private final ProductService service;

    @Autowired
    public ProductValidator(ProductService service) {
        this.service = service;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;

        if(service.findProductByName(product.getName()).isPresent()) {
            errors.rejectValue("name","", "This product name is already in use");
        }
    }
}
