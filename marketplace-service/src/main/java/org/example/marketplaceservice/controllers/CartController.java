package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.config.JWTFilter;
import org.example.marketplaceservice.dto.ProductInOrderDTO;
import org.example.marketplaceservice.exceptions.CartIsEmptyException;
import org.example.marketplaceservice.exceptions.ErrorResponse;
import org.example.marketplaceservice.mappers.ProductInOrderMapper;
import org.example.marketplaceservice.models.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final ProductInOrderMapper productInOrderMapper;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    public CartController(ProductInOrderMapper productInOrderMapper) {
        this.productInOrderMapper = productInOrderMapper;
    }

    @GetMapping("/get")
    public List<ProductInOrderDTO> getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user"); // Получение объекта Cart из сессии пользователя.
        if(cart.getCart().isEmpty()) { // Проверка, пустая ли корзина.
            logger.warn("Cart is empty");
            throw new CartIsEmptyException("Your cart is empty"); // Если корзина пуста, выбрасывается исключение.
        }
        logger.info("Successful receipt of cart");
        // Преобразование содержимого корзины в список DTO и возврат его.
        return cart.getCart().stream()
                .map(productInOrderMapper::convertToProductInOrderDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/clear")
    public ResponseEntity<HttpStatus> clearCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user"); // Получение объекта Cart из сессии пользователя.
        cart.clear(); // Очистка корзины.
        logger.info("Successful emptying of cart");
        return ResponseEntity.ok(HttpStatus.OK); // Возврат успешного ответа.
    }

    // Обработчики исключений

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(CartIsEmptyException e){
        // Создание объекта ответа с ошибкой.
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Возврат ответа с ошибкой и статусом 400 BAD_REQUEST.
    }
}
