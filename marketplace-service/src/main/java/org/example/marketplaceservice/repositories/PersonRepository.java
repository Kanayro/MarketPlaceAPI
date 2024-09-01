package org.example.marketplaceservice.repositories;

import org.example.marketplaceservice.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person,Integer> {

    Optional<Person> findByLogin(String login);
}
