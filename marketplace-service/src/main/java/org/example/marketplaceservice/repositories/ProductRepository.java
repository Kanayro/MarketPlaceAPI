package org.example.marketplaceservice.repositories;

import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByName(String name);
}
