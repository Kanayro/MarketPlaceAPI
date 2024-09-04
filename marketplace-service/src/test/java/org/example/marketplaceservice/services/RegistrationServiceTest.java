package org.example.marketplaceservice.services;

import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private PersonRepository personRepository; // Мок для PersonRepository

    @Mock
    private PasswordEncoder passwordEncoder; // Мок для PasswordEncoder

    @InjectMocks
    private RegistrationService registrationService; // Сервис, который мы тестируем

    @Test
    public void testRegister() {
        Person person = new Person();
        person.setPassword("plainPassword");

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);

        registrationService.register(person);

        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(personRepository, times(1)).save(person);

        assertEquals(encodedPassword, person.getPassword());
        assertEquals("ROLE_USER", person.getRole());
    }
}
