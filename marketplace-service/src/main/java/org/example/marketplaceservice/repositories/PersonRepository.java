package org.example.marketplaceservice.repositories;

import org.example.marketplaceservice.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Integer> {
}
