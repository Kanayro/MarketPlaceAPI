package org.example.marketplaceservice.controllers;

import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.OrderDTO;
import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.example.marketplaceservice.mappers.OrderMapper;
import org.example.marketplaceservice.models.*;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.OrderService;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.util.KafkaProducer;
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


import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private OrderService orderService;

    @Mock
    private PersonService personService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private KafkaProducer producer;

    @InjectMocks
    private OrderController controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldCreateOrderWhenCartIsNotEmpty() throws Exception {
        Cart cart = new Cart();
        Cart cartSpy = spy(cart);
        Product product = new Product();
        product.setCount(10);
        product.setIsCount(true);
        cartSpy.addProduct(product,2);
        Person person = new Person();
        String login = "login";
        person.setLogin(login);
        when(jwtUtil.getJWT(any())).thenReturn("token");

        JWTDTO jwtdto = new JWTDTO();

        jwtdto.setLogin(login);
        when(jwtUtil.validateTokenAndRetrieveClaim("token")).thenReturn(jwtdto);
        when(personService.findByLogin(any())).thenReturn(person);

        Order order = new Order();
        order.setId(1);
        ProductInOrder productInOrder = Mockito.mock(ProductInOrder.class);
        productInOrder.setCount(10);

        order.setProducts(Arrays.asList(productInOrder));

        when(orderService.createOrder(cartSpy.getCart(), person)).thenReturn(order);

        mockMvc.perform(get("/order/create")
                        .sessionAttr("user", cartSpy))
                .andExpect(status().isOk());

        verify(orderService,times(1)).save(order);
        verify(producer, times(1)).sendMessage(any(OrderMessageDTO.class));
        verify(cartSpy, times(1)).clear();
    }

    @Test
    public void shouldNotCreateOrderWhenCartIsEmpty() throws Exception {
        Cart cart = new Cart();

        mockMvc.perform(get("/order/create")
                        .sessionAttr("user", cart))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldGetOrders() throws Exception {
        Person person = new Person();
        String login = "login";
        person.setLogin(login);
        when(jwtUtil.getJWT(any())).thenReturn("token");

        JWTDTO jwtdto = new JWTDTO();

        jwtdto.setLogin(login);
        when(jwtUtil.validateTokenAndRetrieveClaim("token")).thenReturn(jwtdto);
        when(personService.findByLogin(any())).thenReturn(person);
        int cost = 100;
        Order order = new Order();
        Order order1 = new Order();
        order.setPerson(person);
        when(orderService.getOrdersByPerson(person)).thenReturn(Arrays.asList(order,order1));
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCost(cost);
        OrderDTO orderDTO1 = new OrderDTO();
        orderDTO1.setCost(cost);
        when(orderMapper.convertToOrderDTO(any())).thenReturn(orderDTO).thenReturn(orderDTO1);

        mockMvc.perform(get("/order/get")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cost").value(cost))
                .andExpect(jsonPath("$[1].cost").value(cost));
    }

    @Test
    public void shouldGetOrderById() throws Exception {

        int id = 1;
        Order order = new Order();
        order.setId(id);
        int cost = 100;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCost(100);
        when(orderService.getOrder(id)).thenReturn(order);
        when(orderMapper.convertToOrderDTO(order)).thenReturn(orderDTO);

        mockMvc.perform(get("/order/{id}/get",id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(cost));
        verify(orderService, times(1)).getOrder(id);
    }

}