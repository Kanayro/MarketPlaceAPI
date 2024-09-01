package org.example.marketplaceservice.exceptions;

public class CartIsEmptyException extends RuntimeException {
    public CartIsEmptyException(String msg) {
        super(msg);
    }
}
