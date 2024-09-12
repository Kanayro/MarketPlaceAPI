package org.example.marketplaceservice.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplaceservice.controllers.ProductController;
import org.example.marketplaceservice.dto.ProductDTO;
import org.example.marketplaceservice.mappers.ProductMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.services.ProductService;
import org.example.marketplaceservice.util.ProductValidator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Testcontainers
public class ProductControllerIntegrationTest {

//    @Container
//    @ServiceConnection
//    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductValidator validator;

    private ProductController controller;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback
    public void shouldSaveProductDoExist() throws Exception {
        ProductDTO productDTO = new ProductDTO("Cola3",100,11,true);
        String requestBody = mapper.writeValueAsString(productDTO);
        System.out.println(requestBody);
        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void shouldGetProduct() throws Exception {
        int id = 1;
        ProductDTO productDTO = new ProductDTO("Milk",90,190,true);
        mockMvc.perform(get("/product/{id}",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.price").value(productDTO.getPrice()))
                .andExpect(jsonPath("$.count").value(productDTO.getCount()));


    }

    @Test
    @WithMockUser
    public void shouldGetProducts() throws Exception {

        String name2 = "Milk";
        String name1 = "Chocolate";

        mockMvc.perform(get("/product/getProducts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(name1))
                .andExpect(jsonPath("$[1].name").value(name2));
    }

    @Test
    @WithMockUser
    public void shouldAddProductToCartDoExist() throws Exception {
        int id = 1;
        int count = 2;

        Cart cart = new Cart();
        mockMvc.perform(get("/product/{id}/add",id)
                        .sessionAttr("user", cart)
                        .param("count", String.valueOf(count)))
                .andExpect(status().isOk());
    }

}
