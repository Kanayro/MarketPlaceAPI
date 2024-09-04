package org.example.marketplaceservice.services;

import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.repositories.PersonRepository;
import org.example.marketplaceservice.security.PersonDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonDetailsServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonDetailsService service;

    @Test
    public void shouldGetUserByUsernameDoExist() {
        Person expectedPerson = new Person();
        String login = "Person";
        expectedPerson.setLogin(login);

        when(personRepository.findByLogin(login)).thenReturn(Optional.of(expectedPerson));

        PersonDetails details = (PersonDetails) service.loadUserByUsername(login);

        Person actualPerson = details.getPerson();

        assertEquals(expectedPerson, actualPerson);
        assertEquals(login, actualPerson.getLogin());
        verify(personRepository, times(1)).findByLogin(login);
    }

    @Test
    public void shouldGetUserByUsernameDoesntExist() {
        String login = "Person";
        when(personRepository.findByLogin(login)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(login));

    }
}
