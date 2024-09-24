package org.example.orderservice.repositories;

import org.example.orderservice.models.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Integer> {
}
