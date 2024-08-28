package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.dto.JWTDTO;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final JWTUtil jwtUtil;
    private final Cart cart;

    @Autowired
    public CartController(JWTUtil jwtUtil, Cart cart) {
        this.jwtUtil = jwtUtil;
        this.cart = cart;
    }

    @GetMapping("/get")
    public List<Product> getCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("user");

        return cart.getCart();
    }
}
