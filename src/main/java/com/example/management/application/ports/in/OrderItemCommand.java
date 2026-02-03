package com.example.management.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for order item in create-order and similar commands.
 */
public record OrderItemCommand(UUID productId, int quantity, BigDecimal unitPriceAmount, String unitPriceCurrency) {

    public OrderItemCommand(UUID productId, int quantity, BigDecimal unitPriceAmount) {
        this(productId, quantity, unitPriceAmount, "USD");
    }
}
