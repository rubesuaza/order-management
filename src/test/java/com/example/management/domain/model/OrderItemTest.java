package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderItem Entity")
class OrderItemTest {

    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final Money UNIT_PRICE = new Money(new BigDecimal("10.00"));

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateWithValidQuantityAndPrice() {
            OrderItem item = new OrderItem(PRODUCT_ID, 2, UNIT_PRICE);
            assertThat(item.getProductId()).isEqualTo(PRODUCT_ID);
            assertThat(item.getQuantity()).isEqualTo(2);
            assertThat(item.getUnitPrice()).isEqualTo(UNIT_PRICE);
        }

        @Test
        void shouldThrowWhenQuantityIsZero() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 0, UNIT_PRICE))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        void shouldThrowWhenQuantityIsNegative() {
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, -1, UNIT_PRICE))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        void shouldThrowWhenUnitPriceIsNegative() {
            Money negativePrice = new Money(new BigDecimal("-5.00"));
            assertThatThrownBy(() -> new OrderItem(PRODUCT_ID, 1, negativePrice))
                    .isInstanceOf(InvalidItemException.class)
                    .hasMessageContaining("price");
        }

        @Test
        void shouldAllowZeroUnitPrice() {
            Money zeroPrice = new Money(BigDecimal.ZERO);
            OrderItem item = new OrderItem(PRODUCT_ID, 1, zeroPrice);
            assertThat(item.getUnitPrice().getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("line total")
    class LineTotal {
        @Test
        void shouldCalculateLineTotalAsUnitPriceTimesQuantity() {
            OrderItem item = new OrderItem(PRODUCT_ID, 3, new Money(new BigDecimal("10.50")));
            Money lineTotal = item.getLineTotal();
            assertThat(lineTotal.getAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
            assertThat(lineTotal.getCurrency()).isEqualTo("USD");
        }
    }
}
