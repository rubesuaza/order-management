package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing a monetary amount with currency.
 * Immutable. Supports addition, subtraction, and multiplication.
 */
public final class Money {

    public static final String DEFAULT_CURRENCY = "USD";

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }

    public Money(BigDecimal amount, String currency) {
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.currency = currency != null && !currency.isBlank() ? currency : DEFAULT_CURRENCY;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                    "Currency mismatch: cannot operate " + this.currency + " with " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }
}
