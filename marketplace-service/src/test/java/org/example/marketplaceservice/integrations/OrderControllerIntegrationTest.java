package org.example.marketplaceservice.integrations;

import jakarta.servlet.http.HttpServletRequest;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.example.marketplaceservice.mappers.OrderMapper;
import org.example.marketplaceservice.models.*;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.OrderService;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.services.ProductService;
import org.example.marketplaceservice.util.KafkaProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Testcontainers
public class OrderControllerIntegrationTest {

//    @Container
//    @ServiceConnection
//    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Container
    @ServiceConnection
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private KafkaProducer producer;

    @Autowired
    private ProductService service;


    @Test
    @WithMockUser
    @Transactional
    @Rollback
    void shouldCreateOrderWhenCartIsNotEmpty() throws Exception {
        Cart cart = new Cart();
        Product product = service.findById(1);
        cart.addProduct(product,2);
        JWTDTO jwtdto = new JWTDTO();
        jwtdto.setLogin("vanva");
        String token = "token";
        when(jwtUtil.getJWT(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtUtil.validateTokenAndRetrieveClaim(token)).thenReturn(jwtdto);
        doNothing().when(producer).sendMessage(any(OrderMessageDTO.class));
        mockMvc.perform(get("/order/create")
                        .sessionAttr("user", cart))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    public void shouldNotCreateOrderWhenCartIsEmpty() throws Exception {
        Cart cart = new Cart();

        mockMvc.perform(get("/order/create")
                        .sessionAttr("user", cart))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser
    public void shouldGetOrders() throws Exception {
        String login = "vanva123";
        String token = "token";
        when(jwtUtil.getJWT(any(HttpServletRequest.class))).thenReturn(token);

        JWTDTO jwtdto = new JWTDTO();
        jwtdto.setLogin(login);
        when(jwtUtil.validateTokenAndRetrieveClaim(token)).thenReturn(jwtdto);

        mockMvc.perform(get("/order/get")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cost").value(900));
    }

    @Test
    @WithMockUser
    public void shouldGetOrderById() throws Exception {
        int id = 1;

        mockMvc.perform(get("/order/{id}/get",id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(900));

    }

}
