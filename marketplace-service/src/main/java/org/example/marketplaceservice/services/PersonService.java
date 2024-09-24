package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.PersonNotFoundException;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Сервис для получения, обновления и удаления людей
@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findByLogin(String login) {
        Optional<Person> person = personRepository.findByLogin(login);

        if(person.isEmpty()){
            throw new UsernameNotFoundException("A user with this login was not found");
        }

        return person.get();
    }

    @Transactional
    public void update(Person updatedPerson, int id) {
        Optional<Person> person = personRepository.findById(id);
        if(person.isEmpty()) {
            throw new PersonNotFoundException("A user with this id was not found");
        }
        updatedPerson.setId(id);
        personRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        Optional<Person> person = personRepository.findById(id);
        if(person.isEmpty()) {
            throw new PersonNotFoundException("A user with this id was not found");
        }
        personRepository.delete(person.get());
    }

    public Optional<Person> findPersonByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public Optional<Person> findPersonByEmail(String email) {
        return personRepository.findPersonByEmail(email);
    }
}
