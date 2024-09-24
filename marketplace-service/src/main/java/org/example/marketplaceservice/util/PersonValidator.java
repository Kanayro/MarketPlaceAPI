package org.example.marketplaceservice.util;

import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//Класс для валидации людей при регистрации
@Component
public class PersonValidator implements Validator {

    private final PersonService service;

    @Autowired
    public PersonValidator(PersonService service) {
        this.service = service;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        if(service.findPersonByLogin(person.getLogin()).isPresent()) {
            errors.rejectValue("login","","This login is already in use");
        }
        if(service.findPersonByEmail(person.getEmail()).isPresent()) {
            errors.rejectValue("email","","This email is already in use");
        }
    }
}
