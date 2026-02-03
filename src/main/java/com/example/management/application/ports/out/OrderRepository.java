package com.example.management.application.ports.out;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for order persistence.
 * Adapters (JPA, in-memory) implement this interface.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    boolean existsById(UUID id);
}
