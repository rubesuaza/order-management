package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a line item in an order.
 * Quantity must be strictly greater than zero; unit price cannot be negative.
 */
public final class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (quantity <= 0) {
            throw new InvalidItemException("OrderItem quantity must be strictly greater than zero");
        }
        if (unitPrice.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("OrderItem unit price cannot be negative");
        }
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    /**
     * Line total = unitPrice * quantity.
     */
    public Money getLineTotal() {
        return unitPrice.multiply(quantity);
    }
}
