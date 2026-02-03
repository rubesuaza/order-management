package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Order Aggregate")
class OrderTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final Money TEN_USD = new Money(new BigDecimal("10.00"));
    private static final Money FIVE_USD = new Money(new BigDecimal("5.00"));

    private OrderItem item(Money unitPrice, int qty) {
        return new OrderItem(UUID.randomUUID(), qty, unitPrice);
    }

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateOrderWithAtLeastOneItem() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getItems()).hasSize(1);
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
        }

        @Test
        void shouldThrowWhenCreatingOrderWithNoItems() {
            assertThatThrownBy(() -> new Order(UUID.randomUUID(), CUSTOMER_ID, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one");
        }

        @Test
        void shouldCalculateTotalAsSumOfLineTotals() {
            OrderItem a = item(new Money(new BigDecimal("10.00")), 2);
            OrderItem b = item(new Money(new BigDecimal("5.00")), 3);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(a, b));
            // 20 + 15 = 35
            assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("35.00"));
        }
    }

    @Nested
    @DisplayName("mark as paid")
    class MarkAsPaid {
        @Test
        void shouldAllowPaidWhenTotalAtLeastTenUsd() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            order.markAsPaid();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        void shouldThrowWhenMarkingPaidIfTotalLessThanTenUsd() {
            OrderItem item = item(FIVE_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            assertThatThrownBy(order::markAsPaid)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("10");
        }

        @Test
        void shouldThrowWhenMarkingPaidIfNotPending() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            order.markAsPaid();
            assertThatThrownBy(order::markAsPaid)
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {
        @Test
        void shouldAllowCancelWhenPending() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void shouldAllowCancelWhenPaid() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            order.markAsPaid();
            order.cancel();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void shouldThrowWhenCancellingShippedOrder() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            order.markAsPaid();
            order.ship();
            assertThatThrownBy(order::cancel)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("CANCELLED");
        }
    }

    @Nested
    @DisplayName("ship")
    class Ship {
        @Test
        void shouldAllowShipWhenPaid() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            order.markAsPaid();
            order.ship();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        }

        @Test
        void shouldThrowWhenShippingPendingOrder() {
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(UUID.randomUUID(), CUSTOMER_ID, List.of(item));
            assertThatThrownBy(order::ship)
                    .isInstanceOf(InvalidOrderStateException.class)
                    .hasMessageContaining("SHIPPED");
        }
    }

    @Nested
    @DisplayName("identity and timestamps")
    class Identity {
        @Test
        void shouldExposeOrderIdCustomerIdAndCreatedAt() {
            UUID orderId = UUID.randomUUID();
            OrderItem item = item(TEN_USD, 1);
            Order order = new Order(orderId, CUSTOMER_ID, List.of(item));
            assertThat(order.getId()).isEqualTo(orderId);
            assertThat(order.getCustomerId()).isEqualTo(CUSTOMER_ID);
            assertThat(order.getCreatedAt()).isNotNull();
        }
    }
}
