package org.example.marketplaceservice.repositories;

import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findProductsByOrders(Order order);
}
