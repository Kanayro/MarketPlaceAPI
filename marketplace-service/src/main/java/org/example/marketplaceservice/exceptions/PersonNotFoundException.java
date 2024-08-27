package org.example.marketplaceservice.exceptions;

public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException(String msg) {
        super(msg);
    }
}
