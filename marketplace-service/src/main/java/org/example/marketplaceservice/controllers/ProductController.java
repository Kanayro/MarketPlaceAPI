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

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addProduct(@RequestBody @Valid ProductDTO productDTO, BindingResult result) {
        Product product = productMapper.convertToProduct(productDTO);
        if(result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = result.getFieldErrors();

            for(FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
            }
            throw new ProductNotCreatedException(errorMsg.toString());
        }
        productService.save(product);

        return ResponseEntity.ok(HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable("id") int id){
        return productMapper.convertToProductDTO(productService.findById(id));
    }

    @GetMapping("/getProducts")
    public List<ProductDTO> getProducts() {
        return productService.getProducts().stream().map(productMapper::convertToProductDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}/add")
    public ResponseEntity<HttpStatus> addProductToCart(@PathVariable int id, HttpSession session, @RequestParam("count") int count) {
        Product product = productService.findById(id);
        Cart cart = (Cart) session.getAttribute("user");
        cart.addProduct(product,count);
        session.setAttribute("user",cart);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Exception handlers

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ProductNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ProductNotEnoughException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(ProductNotCreatedException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
