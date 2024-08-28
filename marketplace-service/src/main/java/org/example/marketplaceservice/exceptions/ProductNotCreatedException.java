package org.example.marketplaceservice.exceptions;

public class ProductNotCreatedException extends RuntimeException{
    public ProductNotCreatedException(String msg) {
        super(msg);
    }
}
