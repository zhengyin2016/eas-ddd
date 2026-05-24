package com.eas.crm.domain.contract;

public enum PaymentPlanStatus {
    PENDING("待支付"),
    PAID("已支付"),
    OVERDUE("逾期");

    private final String description;

    PaymentPlanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
