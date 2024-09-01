package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.dto.OrderDTO;
import org.example.marketplaceservice.exceptions.CartIsEmptyException;
import org.example.marketplaceservice.exceptions.ErrorResponse;
import org.example.marketplaceservice.exceptions.OrderNotFoundException;
import org.example.marketplaceservice.mappers.OrderMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.OrderService;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.util.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final JWTUtil jwtUtil;
    private final OrderService orderService;
    private final PersonService personService;
    private final OrderMapper orderMapper;
    private final KafkaProducer producer;

    @Autowired
    public OrderController(JWTUtil jwtUtil, OrderService orderService, PersonService personService, OrderMapper orderMapper, KafkaProducer producer) {
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
        this.personService = personService;
        this.orderMapper = orderMapper;
        this.producer = producer;
    }

    @GetMapping("/create")
    public ResponseEntity<HttpStatus> createOrder(HttpServletRequest request, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user");
        if(cart.getCart().isEmpty()) {
            throw new CartIsEmptyException("Your cart is empty");
        }
        Person person = personService.findByLogin( jwtUtil.validateTokenAndRetrieveClaim(jwtUtil.getJWT(request)).getLogin());
        Order order = orderService.createOrder(cart.getCart(),person);
        orderService.save(order);
        producer.sendMessage("Order " +order.getId());
        cart.clear();

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get")
    public List<OrderDTO> getOrders(HttpServletRequest request) {
        Person person = personService.findByLogin(jwtUtil.validateTokenAndRetrieveClaim(jwtUtil.getJWT(request)).getLogin());
        List<Order> orders = orderService.getOrdersByPerson(person);
        return orders.stream().map(orderMapper::convertToOrderDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}/get")
    public OrderDTO getOrder(@PathVariable("id") int id) {
        return orderMapper.convertToOrderDTO(orderService.getOrder(id));
    }

    //Exception handlers

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(OrderNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(CartIsEmptyException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
