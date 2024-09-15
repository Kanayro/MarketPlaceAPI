package org.example.marketplaceservice.integrations;

import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.example.marketplaceservice.models.*;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.OrderService;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.services.ProductService;
import org.example.marketplaceservice.services.RegistrationService;
import org.example.marketplaceservice.util.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
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


import java.util.Arrays;
import java.util.Date;

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

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Container
    @ServiceConnection
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @MockBean
    private KafkaProducer producer;

    @Autowired
    private ProductService productService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PersonService personService;

    @Autowired
    private OrderService service;

    private String token;

    @BeforeEach
    public void setUp() {
        Product product = new Product(1,"Product1",60,100,true);
        productService.save(product);

        JWTDTO jwtdto = new JWTDTO();
        jwtdto.setId(1);
        jwtdto.setLogin("login");
        jwtdto.setRole("ROLE_USER");

        Person person = new Person(1,"name","email","login","password","", null);
        Order order = new Order(1,900,person,new Date(), "READY");
        order.setProducts(Arrays.asList(new ProductInOrder(1,"Product1",60,1)));

        registrationService.register(person);
        service.save(order);
        person.setOrders(Arrays.asList(order));
        personService.update(person,1);

        token = "Bearer " + jwtUtil.generateToken(jwtdto);
    }

    @Test
    @WithMockUser
    @Transactional
    @Rollback
    void shouldCreateOrderWhenCartIsNotEmpty() throws Exception {
        Cart cart = new Cart();
        Product product = productService.findById(1);
        cart.addProduct(product,2);
        doNothing().when(producer).sendMessage(any(OrderMessageDTO.class));
        mockMvc.perform(get("/order/create")
                        .sessionAttr("user", cart)
                        .header("Authorization",token))
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

        mockMvc.perform(get("/order/get")
                        .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization",token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cost").value(900));
    }

    @Test
    @WithMockUser
    public void shouldGetOrderById() throws Exception {
        int id = 1;

        mockMvc.perform(get("/order/{id}/get",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(900));

    }

}
