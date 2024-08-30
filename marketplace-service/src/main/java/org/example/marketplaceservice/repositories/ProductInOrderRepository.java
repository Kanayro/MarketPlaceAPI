package org.example.marketplaceservice.repositories;

import org.example.marketplaceservice.models.ProductInOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInOrderRepository extends JpaRepository<ProductInOrder, Integer> {
}
