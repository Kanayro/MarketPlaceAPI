package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.dto.OrderDTO;
import org.example.marketplaceservice.mappers.OrderMapper;
import org.example.marketplaceservice.mappers.PersonMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.OrderService;
import org.example.marketplaceservice.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final JWTUtil jwtUtil;
    private final OrderService orderService;
    private final PersonService personService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(JWTUtil jwtUtil, OrderService orderService, PersonService personService, OrderMapper orderMapper) {
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
        this.personService = personService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/create")
    public ResponseEntity<HttpStatus> createOrder(HttpServletRequest request, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user");
        Person person = personService.findByLogin( jwtUtil.validateTokenAndRetrieveClaim(jwtUtil.getJWT(request)).getLogin());
        Order order = orderService.createOrder(cart.getCart(),person);
        orderService.save(order);

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
}
