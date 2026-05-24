package com.eas.crm.domain.opportunity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final int SCALE = 2;

    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.scale() > SCALE) {
            amount = amount.setScale(SCALE, ROUNDING);
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(SCALE, ROUNDING));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO.setScale(SCALE, ROUNDING));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount).setScale(SCALE, ROUNDING));
    }

    public Money subtract(Money other) {
        BigDecimal result = this.amount.subtract(other.amount).setScale(SCALE, ROUNDING);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(result);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier).setScale(SCALE, ROUNDING));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
