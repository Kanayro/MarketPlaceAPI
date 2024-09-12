package org.example.marketplaceservice.integrations;

import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Testcontainers
public class CartControllerIntegrationTest {

//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
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



        mockMvc.perform(get("/cart/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("user", cart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }

    @Test
    @WithMockUser
    public void shouldNotGetCartWhenCartIsEmpty() throws Exception {
        Cart cart = new Cart();

        mockMvc.perform(get("/cart/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("user", cart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void shouldClearCart() throws Exception {
        Cart cart = Mockito.mock(Cart.class);

        mockMvc.perform(get("/cart/clear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr("user", cart))
                .andExpect(status().isOk());
        verify(cart, times(1)).clear();
    }

}
