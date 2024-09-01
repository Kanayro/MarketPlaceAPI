package org.example.marketplaceservice.repositories;

import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {

    List<Order> findOrdersByPerson(Person person);
}
