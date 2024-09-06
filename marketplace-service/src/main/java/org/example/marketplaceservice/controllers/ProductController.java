package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.marketplaceservice.dto.ProductDTO;
import org.example.marketplaceservice.exceptions.ErrorResponse;
import org.example.marketplaceservice.exceptions.ProductNotCreatedException;
import org.example.marketplaceservice.exceptions.ProductNotEnoughException;
import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.example.marketplaceservice.mappers.ProductMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.services.ProductService;
import org.example.marketplaceservice.util.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductValidator validator;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper, ProductValidator validator) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.validator = validator;
    }

    // Метод для добавления нового продукта.
    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addProduct(@RequestBody @Valid ProductDTO productDTO, BindingResult result) {
        // Преобразуем DTO (Data Transfer Object) в сущность Product.
        Product product = productMapper.convertToProduct(productDTO);

        // Выполняем валидацию данных продукта.
        validator.validate(product, result);

        // Проверяем, возникли ли ошибки во время валидации.
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();

            // Составляем сообщение с ошибками валидации.
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            // Если есть ошибки, выбрасываем исключение с сообщением об ошибках.
            throw new ProductNotCreatedException(errorMsg.toString());
        }

        // Сохраняем продукт с помощью сервиса.
        productService.save(product);

        // Возвращаем статус 200 OK в ответе.
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Метод для получения продукта по его идентификатору.
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable("id") int id) {
        // Получаем продукт из сервиса и преобразуем его в DTO.
        return productMapper.convertToProductDTO(productService.findById(id));
    }

    // Метод для получения списка всех продуктов.
    @GetMapping("/getProducts")
    public List<ProductDTO> getProducts() {
        // Получаем список всех продуктов, преобразуем их в DTO и возвращаем.
        return productService.getProducts().stream()
                .map(productMapper::convertToProductDTO) // Преобразуем каждую сущность Product в ProductDTO.
                .collect(Collectors.toList()); // Собираем в список.
    }

    // Метод для добавления продукта в корзину пользователя.
    @GetMapping("/{id}/add")
    public ResponseEntity<HttpStatus> addProductToCart(@PathVariable int id, HttpSession session, @RequestParam("count") int count) {
        // Находим продукт по его идентификатору.
        Product product = productService.findById(id);

        // Получаем объект корзины из сессии пользователя.
        Cart cart = (Cart) session.getAttribute("user");
        // Добавляем продукт в корзину с указанным количеством.
        cart.addProduct(product, count);
        // Сохраняем обновленную корзину обратно в сессию.
        session.setAttribute("user", cart);

        // Возвращаем статус 200 OK в ответе.
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Обработчики исключений для обработки определенных ошибок.

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ProductNotFoundException e) {
        // Создаем объект ErrorResponse на основе сообщения исключения и текущего времени.
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        // Возвращаем ответ с кодом NOT_FOUND (404).
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ProductNotEnoughException e) {
        // Обработка исключений, связанных с недостаточным количеством продукта.
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Возвращаем BAD_REQUEST (400).
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ProductNotCreatedException e) {
        // Обработка исключений, возникающих при создании продукта.
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Возвращаем BAD_REQUEST (400).
    }
}
