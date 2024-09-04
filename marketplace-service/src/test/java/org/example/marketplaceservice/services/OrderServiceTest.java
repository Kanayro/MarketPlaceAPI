package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.OrderNotFoundException;
import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.models.ProductInOrder;
import org.example.marketplaceservice.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldUpdateDateAndSaveOrder() {
        ProductInOrder product1 = Mockito.mock(ProductInOrder.class);
        ProductInOrder product2 = Mockito.mock(ProductInOrder.class);

        List<ProductInOrder> products = Arrays.asList(product1, product2);

        Order order = new Order();
        order.setProducts(products);

        orderService.save(order);

        for (ProductInOrder product : products) {
           verify(productService, times(1)).updateProduct(product);
        }

        assertNotNull(order.getDateOfCreate());
        assertEquals("ASSEMBLY",order.getStatus());
        verify(orderRepository,times(1)).save(order);
    }

    @Test
    public void orderShouldCreate() {
        ProductInOrder product1 = Mockito.mock(ProductInOrder.class);
        ProductInOrder product2 = Mockito.mock(ProductInOrder.class);
        when(product1.getPrice()).thenReturn(200);
        when(product2.getPrice()).thenReturn(200);

        List<ProductInOrder> products = Arrays.asList(product1, product2);

        Person person = Mockito.mock(Person.class);

        Order order = orderService.createOrder(products,person);

        assertEquals(person,order.getPerson());
        assertEquals(products,order.getProducts());
        assertEquals(400,order.getCost());

    }

    @Test
    public void shouldGetOrderDoExist() {
        Order expectedOrder = new Order();
        expectedOrder.setId(1);
        when(orderRepository.findById(1)).thenReturn(Optional.of(expectedOrder));

        Order actualOrder = orderService.getOrder(1);

        assertEquals(expectedOrder,actualOrder);
        verify(orderRepository,times(1)).findById(1);
    }

    @Test
    public void shouldGetOrderDoesntExist() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,() -> orderService.getOrder(1));
        verify(orderRepository,times(1)).findById(1);
    }

    @Test
    public void shouldReturnOrdersByPerson() {
        Person person = new Person();

        List<Order> expectedOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findOrdersByPerson(person)).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getOrdersByPerson(person);

        assertEquals(expectedOrders, actualOrders);
        verify(orderRepository,times(1)).findOrdersByPerson(person);

    }

    @Test
    public void shouldUpdateStatusGetOrderDoExist() {
        Order order = new Order();
        order.setId(1);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus("SENT",1);

        assertEquals("SENT",order.getStatus());
        verify(orderRepository,times(1)).findById(1);
    }

    @Test
    public void shouldUpdateStatusGetOrderDoesntExist() {
        Order order = new Order();
        order.setId(1);
        order.setStatus("ASSEMBLY");
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,() -> orderService.updateOrderStatus("SENT",1));
        assertNotEquals(order.getStatus(), "SENT");
        verify(orderRepository,times(1)).findById(1);
    }


}
