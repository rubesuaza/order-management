package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Input port: create a new order.
 */
public interface CreateOrderUseCase {

    /**
     * Creates an order for the given customer with the given items.
     *
     * @param customerId customer identifier
     * @param items      at least one order item (productId, quantity, unitPrice)
     * @return the created order
     */
    Order create(UUID customerId, List<OrderItemCommand> items);
}
