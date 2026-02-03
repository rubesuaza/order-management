package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Money Value Object")
class MoneyTest {

    private static final String USD = "USD";
    private static final String EUR = "EUR";

    @Nested
    @DisplayName("creation")
    class Creation {
        @Test
        void shouldCreateWithAmountAndDefaultCurrencyUsd() {
            Money money = new Money(new BigDecimal("10.50"));
            assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("10.50"));
            assertThat(money.getCurrency()).isEqualTo(USD);
        }

        @Test
        void shouldCreateWithAmountAndCurrency() {
            Money money = new Money(new BigDecimal("25.00"), EUR);
            assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
            assertThat(money.getCurrency()).isEqualTo(EUR);
        }
    }

    @Nested
    @DisplayName("addition")
    class Addition {
        @Test
        void shouldAddSameCurrency() {
            Money a = new Money(new BigDecimal("10.00"), USD);
            Money b = new Money(new BigDecimal("5.50"), USD);
            Money result = a.add(b);
            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("15.50"));
            assertThat(result.getCurrency()).isEqualTo(USD);
        }

        @Test
        void shouldThrowWhenAddingDifferentCurrencies() {
            Money usd = new Money(new BigDecimal("10.00"), USD);
            Money eur = new Money(new BigDecimal("5.00"), EUR);
            assertThatThrownBy(() -> usd.add(eur))
                    .isInstanceOf(CurrencyMismatchException.class)
                    .hasMessageContaining("Currency");
        }
    }

    @Nested
    @DisplayName("subtraction")
    class Subtraction {
        @Test
        void shouldSubtractSameCurrency() {
            Money a = new Money(new BigDecimal("10.00"), USD);
            Money b = new Money(new BigDecimal("3.25"), USD);
            Money result = a.subtract(b);
            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("6.75"));
            assertThat(result.getCurrency()).isEqualTo(USD);
        }

        @Test
        void shouldThrowWhenSubtractingDifferentCurrencies() {
            Money usd = new Money(new BigDecimal("10.00"), USD);
            Money eur = new Money(new BigDecimal("5.00"), EUR);
            assertThatThrownBy(() -> usd.subtract(eur))
                    .isInstanceOf(CurrencyMismatchException.class);
        }
    }

    @Nested
    @DisplayName("multiplication")
    class Multiplication {
        @Test
        void shouldMultiplyByScalar() {
            Money money = new Money(new BigDecimal("10.00"), USD);
            Money result = money.multiply(3);
            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("30.00"));
            assertThat(result.getCurrency()).isEqualTo(USD);
        }

        @Test
        void shouldMultiplyByDecimalScalar() {
            Money money = new Money(new BigDecimal("10.50"), USD);
            Money result = money.multiply(2);
            assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("21.00"));
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {
        @Test
        void shouldBeEqualWhenAmountAndCurrencyMatch() {
            Money a = new Money(new BigDecimal("10.00"), USD);
            Money b = new Money(new BigDecimal("10.00"), USD);
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        void shouldNotBeEqualWhenCurrencyDiffers() {
            Money usd = new Money(new BigDecimal("10.00"), USD);
            Money eur = new Money(new BigDecimal("10.00"), EUR);
            assertThat(usd).isNotEqualTo(eur);
        }
    }
}
