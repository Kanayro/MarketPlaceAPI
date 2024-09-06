package org.example.marketplaceservice.util;

import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.services.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonValidatorTest {

    @Mock
    private PersonService service;

    @Mock
    private Errors errors;

    private Person person;

    @InjectMocks
    private PersonValidator validator;

    @BeforeEach
    public void setUp() {
        person = new Person();
    }

    @Test
    public void shouldValidatePersonByLoginWhenLoginAlreadyInUse() {
        when(service.findPersonByLogin(person.getLogin())).thenReturn(Optional.of(new Person()));

        validator.validate(person,errors);

        verify(errors, times(1)).rejectValue(any(),any(),any());
    }

    @Test
    public void shouldValidatePersonByLoginWhenLoginIsAvailable() {
        when(service.findPersonByLogin(person.getLogin())).thenReturn(Optional.empty());

        validator.validate(person,errors);

        verify(errors, never()).rejectValue(any(),any(),any());
    }
}
