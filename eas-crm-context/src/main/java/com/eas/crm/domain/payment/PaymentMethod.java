package com.eas.crm.domain.payment;

public enum PaymentMethod {
    BANK_TRANSFER("银行转账"),
    CHECK("支票"),
    CASH("现金"),
    ELECTRONIC("电子支付");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
