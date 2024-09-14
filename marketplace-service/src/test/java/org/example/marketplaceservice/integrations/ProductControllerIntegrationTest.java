package org.example.marketplaceservice.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplaceservice.dto.ProductDTO;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.repositories.ProductRepository;
import org.junit.jupiter.api.*;
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
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Testcontainers
public class ProductControllerIntegrationTest {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Container
    @ServiceConnection
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        repository.save(new Product(1,"Water",50,120,true));
        repository.save(new Product(2,"Chocolate",50,200,true));
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback
    public void shouldSaveProductDoExist() throws Exception {
        ProductDTO productDTO = new ProductDTO("Cola3",100,11,true);
        String requestBody = mapper.writeValueAsString(productDTO);
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
        ProductDTO productDTO = new ProductDTO("Water",50,120,true);
        mockMvc.perform(get("/product/{id}",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.price").value(productDTO.getPrice()))
                .andExpect(jsonPath("$.count").value(productDTO.getCount()));
    }

    @Test
    @WithMockUser
    public void shouldGetProducts() throws Exception {

        String name1 = "Water";
        String name2 = "Chocolate";

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
