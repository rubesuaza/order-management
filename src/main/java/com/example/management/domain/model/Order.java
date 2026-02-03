package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for the order lifecycle.
 * An order must have at least one item. Total is the sum of (unitPrice * quantity) for all items.
 * Minimum order value to mark as PAID is 10.00 USD.
 */
public final class Order {

    private static final BigDecimal MINIMUM_ORDER_AMOUNT_USD = new BigDecimal("10.00");

    private final UUID id;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private OrderStatus status;

    /** Constructor for new orders (sets createdAt and PENDING status). */
    public Order(UUID id, UUID customerId, List<OrderItem> items) {
        this(id, customerId, LocalDateTime.now(), items, OrderStatus.PENDING);
    }

    /** Constructor for reconstitution from persistence (e.g. repository). */
    public Order(UUID id, UUID customerId, LocalDateTime createdAt, List<OrderItem> items, OrderStatus status) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one OrderItem");
        }
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.items = new ArrayList<>(items);
        this.status = status != null ? status : OrderStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Total amount = sum of (unitPrice * quantity) for all items.
     */
    public Money getTotalAmount() {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(Money::add)
                .orElseThrow(() -> new IllegalStateException("Order must have at least one item"));
    }

    /**
     * Mark order as PAID. Only allowed when status is PENDING and total >= 10.00 USD.
     */
    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be marked as PAID when status is PENDING");
        }
        if (!meetsMinimumOrderAmount()) {
            throw new InvalidOrderStateException("Order total must be at least 10.00 USD to be placed (PAID)");
        }
        this.status = OrderStatus.PAID;
    }

    /** True when order total is at least the minimum required to be placed (PAID). */
    public boolean meetsMinimumOrderAmount() {
        return getTotalAmount().getAmount().compareTo(MINIMUM_ORDER_AMOUNT_USD) >= 0;
    }

    /**
     * Ship the order. Only allowed when status is PAID.
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be SHIPPED when status is PAID");
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * Cancel the order. Only allowed when status is PENDING or PAID.
     */
    public void cancel() {
        if (status != OrderStatus.PENDING && status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order can only be CANCELLED when status is PENDING or PAID");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
