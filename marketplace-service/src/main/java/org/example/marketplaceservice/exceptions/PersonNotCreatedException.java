package org.example.marketplaceservice.exceptions;

public class PersonNotCreatedException extends RuntimeException{

    public PersonNotCreatedException(String msg) {
        super(msg);
    }
}
