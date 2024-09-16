package org.example.marketplaceservice.services;

import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.repositories.PersonRepository;
import org.example.marketplaceservice.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

//Сервис для аутентификации пользователя
@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByLogin(username);

        if(person.isEmpty()){
            throw new UsernameNotFoundException("Login not found");
        }
        return new PersonDetails(person.get());
    }
}
