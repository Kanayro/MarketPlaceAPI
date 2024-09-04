package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.PersonNotFoundException;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.repositories.PersonRepository;
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
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService service;

    @Test
    public void shouldReturnPersonByLoginDoExist() {
        Person expectedPerson = new Person();
        String login = "Person";
        expectedPerson.setLogin(login);

        when(personRepository.findByLogin(login)).thenReturn(Optional.of(expectedPerson));

        Person actualPerson = service.findByLogin(login);

        assertEquals(expectedPerson, actualPerson);
        assertEquals(login, actualPerson.getLogin());
        verify(personRepository, times(1)).findByLogin(login);
    }

    @Test
    public void shouldReturnPersonByLoginDoesntExist() {
        String login = "Person";
        when(personRepository.findByLogin(login)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.findByLogin(login));
        verify(personRepository, times(1)).findByLogin(login);
    }

    @Test
    public void shouldUpdatePersonDoExist() {
        int id = 1;
        Person existingPerson = new Person();
        existingPerson.setId(id);
        when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));

        Person updatedPerson = new Person();

        service.update(updatedPerson, id);

        assertEquals(id, updatedPerson.getId());
        verify(personRepository, times(1)).findById(id);
        verify(personRepository, times(1)).save(updatedPerson);
    }

    @Test
    public void shouldUpdatePersonDoesntExist() {
        int id = 1;
        Person updatedPerson = new Person();

        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> service.update(updatedPerson, id));
        verify(personRepository, times(1)).findById(id);
        verify(personRepository, times(0)).save(updatedPerson);
    }

    @Test
    public void shouldDeletePersonByIdDoExist() {
        int id = 1;
        Person person = new Person();
        person.setId(id);

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        service.delete(id);

        verify(personRepository, times(1)).findById(id);
        verify(personRepository, times(1)).delete(person);
    }

    @Test
    public void shouldDeletePersonByIdDoesntExist() {
        int id = 1;
        Person person = new Person();

        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> service.delete(id));

        verify(personRepository, times(1)).findById(id);
        verify(personRepository, times(0)).delete(person);
    }

}
