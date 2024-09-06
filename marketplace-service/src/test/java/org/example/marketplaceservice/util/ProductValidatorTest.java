package org.example.marketplaceservice.util;

import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class ProductValidatorTest {

    @Mock
    private ProductService service;

    @Mock
    private Errors errors;

    private Product product;

    @InjectMocks
    private ProductValidator validator;

    @BeforeEach
    public void setUp() {
        product = new Product();
    }

    @Test
    public void shouldValidateProductByNameWhenProductNameAlreadyInUse() {
        when(service.findProductByName(product.getName())).thenReturn(Optional.of(new Product()));

        validator.validate(product,errors);

        verify(errors, times(1)).rejectValue(any(),any(),any());
    }

    @Test
    public void shouldValidateProductByNameWhenProductNameIsAvailable() {
        when(service.findProductByName(product.getName())).thenReturn(Optional.empty());

        validator.validate(product,errors);

        verify(errors, never()).rejectValue(any(),any(),any());
    }
}
