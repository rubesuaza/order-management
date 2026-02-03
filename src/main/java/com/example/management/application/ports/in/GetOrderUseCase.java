package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Input port: get an order by id.
 */
public interface GetOrderUseCase {

    Optional<Order> getById(UUID orderId);
}
