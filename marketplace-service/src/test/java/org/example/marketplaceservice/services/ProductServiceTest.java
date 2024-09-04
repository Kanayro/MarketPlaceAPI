package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.models.ProductInOrder;
import org.example.marketplaceservice.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService service;

    @Test
    public void shouldSaveProduct() {
        Product product = new Product();

        productRepository.save(product);

        verify(productRepository,times(1)).save(product);
    }

    @Test
    public void shouldReturnProductDoExist() {
        Product expectedProduct = new Product();
        int id = 1;
        expectedProduct.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(expectedProduct));

        Product actualProduct = service.findById(id);

        assertEquals(actualProduct,expectedProduct);
        assertEquals(id,actualProduct.getId());
        verify(productRepository, times(1)).findById(id);

    }

    @Test
    public void shouldReturnProductDoesntExist() {
        int id = 1;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.findById(id));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    public void shouldDecrementCountAndUpdateProductDoExist() {
        ProductInOrder productInOrder = new ProductInOrder();
        productInOrder.setCount(10);
        String name = "Product";
        productInOrder.setName(name);

        Product product = new Product();
        product.setName(name);
        int count = 10;
        product.setCount(count);

        when(productRepository.findByName(name)).thenReturn(Optional.of(product));

        service.updateProduct(productInOrder);

        assertEquals(product.getCount(), count-productInOrder.getCount());
        assertFalse(product.isCount());
        verify(productRepository,times(1)).findByName(name);
        verify(productRepository,times(1)).save(product);
    }

    @Test
    public void shouldDecrementCountAndUpdateProductDoesntExist() {
        String name = "Product";
        ProductInOrder productInOrder = new ProductInOrder();
        productInOrder.setName(name);
        when(productRepository.findByName(name)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.updateProduct(productInOrder));

        verify(productRepository,times(1)).findByName(name);
    }

    @Test
    public void shouldDeleteProductDoExist() {
        Product product = new Product();
        int id = 1;
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        service.delete(id);

        verify(productRepository,times(1)).findById(id);
        verify(productRepository,times(1)).delete(product);
    }

    @Test
    public void shouldDeleteProductDoesntExist() {
        int id = 1;
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.delete(id));
        verify(productRepository,times(1)).findById(id);
    }
}
