package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Input port: change order status (mark paid, ship, cancel).
 */
public interface OrderStatusUseCase {

    Optional<Order> markAsPaid(UUID orderId);

    Optional<Order> ship(UUID orderId);

    Optional<Order> cancel(UUID orderId);
}
