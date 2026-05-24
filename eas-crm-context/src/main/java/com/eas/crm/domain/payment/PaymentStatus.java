package com.eas.crm.domain.payment;

public enum PaymentStatus {
    PLANNED("计划中"),
    PAID("已支付"),
    OVERDUE("逾期"),
    CANCELLED("已取消");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(PaymentStatus newStatus) {
        return switch (this) {
            case PLANNED -> newStatus == PAID || newStatus == OVERDUE || newStatus == CANCELLED;
            case PAID -> false; // 终态
            case OVERDUE -> newStatus == PAID || newStatus == CANCELLED;
            case CANCELLED -> false; // 终态
        };
    }
}
