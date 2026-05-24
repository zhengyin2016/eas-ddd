package com.eas.crm.domain.contract;

public enum ContractStatus {
    DRAFT("草稿"),
    UNDER_REVIEW("审核中"),
    APPROVED("已审批"),
    ACTIVE("执行中"),
    COMPLETED("已完成"),
    TERMINATED("已终止");

    private final String description;

    ContractStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(ContractStatus newStatus) {
        return switch (this) {
            case DRAFT -> newStatus == UNDER_REVIEW || newStatus == TERMINATED;
            case UNDER_REVIEW -> newStatus == APPROVED || newStatus == DRAFT || newStatus == TERMINATED;
            case APPROVED -> newStatus == ACTIVE || newStatus == TERMINATED;
            case ACTIVE -> newStatus == COMPLETED || newStatus == TERMINATED;
            case COMPLETED -> false; // 终态
            case TERMINATED -> false; // 终态
        };
    }
}
