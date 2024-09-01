package org.example.marketplaceservice.exceptions;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String msg) {
        super(msg);
    }
}
