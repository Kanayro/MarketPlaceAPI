package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.dto.ProductInOrderDTO;
import org.example.marketplaceservice.mappers.ProductInOrderMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.models.ProductInOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final ProductInOrderMapper productInOrderMapper;

    @Autowired
    public CartController(ProductInOrderMapper productInOrderMapper) {
        this.productInOrderMapper = productInOrderMapper;
    }

    @GetMapping("/get")
    public List<ProductInOrderDTO> getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user");
        return cart.getCart().stream().map(productInOrderMapper::convertToProductInOrderDTO).collect(Collectors.toList());
    }

    @GetMapping("/clear")
    public ResponseEntity<HttpStatus> clearCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user");
        cart.clear();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
