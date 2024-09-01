package org.example.marketplaceservice.exceptions;

public class ProductNotEnoughException extends RuntimeException {
    public ProductNotEnoughException(String msg) {
        super(msg);
    }
}
