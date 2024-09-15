package org.example.marketplaceservice.controllers;

import org.example.marketplaceservice.dto.ProductInOrderDTO;
import org.example.marketplaceservice.mappers.ProductInOrderMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Mock
    private ProductInOrderMapper productInOrderMapper;

    @InjectMocks
    private CartController controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldGetCartWhenCartIsNotEmpty() throws Exception {
        Product product1 = new Product();
        Product product2 = new Product();
        product1.setIsCount(true);
        product2.setIsCount(true);
        product1.setCount(10);
        product2.setCount(10);

        String name1 = "name1";
        String name2 = "name2";

        product1.setName(name1);
        product2.setName(name2);

        Cart cart = new Cart();

        cart.addProduct(product1,1);
        cart.addProduct(product2,1);

        ProductInOrderDTO productInOrder1 = new ProductInOrderDTO();
        ProductInOrderDTO productInOrder2 = new ProductInOrderDTO();
        productInOrder1.setName(name1);
        productInOrder2.setName(name2);

        when(productInOrderMapper.convertToProductInOrderDTO(any())).thenReturn(productInOrder1).thenReturn(productInOrder2);

        mockMvc.perform(get("/cart/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("user", cart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }

    @Test
    public void shouldNotGetCartWhenCartIsEmpty() throws Exception {
        Cart cart = new Cart();

        mockMvc.perform(get("/cart/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("user", cart))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldClearCart() throws Exception {
        Cart cart = Mockito.mock(Cart.class);

        mockMvc.perform(get("/cart/clear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("user", cart))
                .andExpect(status().isOk());
        verify(cart, times(1)).clear();
    }

}