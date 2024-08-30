package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.dto.JWTDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final JWTUtil jwtUtil;
    private final OrderService orderService;
    private final PersonService personService;

    @Autowired
    public OrderController(JWTUtil jwtUtil, OrderService orderService, PersonService personService) {
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
        this.personService = personService;
    }

    @GetMapping("/create")
    public ResponseEntity<HttpStatus> createOrder(HttpServletRequest request, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user");

        JWTDTO jwtdto = jwtUtil.validateTokenAndRetrieveClaim(jwtUtil.getJWT(request));

        Person person = personService.findByLogin(jwtdto.getLogin());
        Order order = orderService.createOrder(cart.getCart(),person);
        orderService.save(order);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
