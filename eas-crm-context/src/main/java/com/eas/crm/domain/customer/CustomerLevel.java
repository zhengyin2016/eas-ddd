package com.eas.crm.domain.customer;

public enum CustomerLevel {
    A("A级", "VIP客户，累计交易额≥100万或年交易≥10次"),
    B("B级", "优质客户，累计交易额≥50万或年交易≥5次"),
    C("C级", "普通客户，累计交易额≥10万或年交易≥1次"),
    D("D级", "潜力客户，新客户");

    private final String code;
    private final String description;

    CustomerLevel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public CustomerLevel nextLevel() {
        return switch (this) {
            case D -> C;
            case C -> B;
            case B -> A;
            case A -> A;
        };
    }

    public CustomerLevel previousLevel() {
        return switch (this) {
            case A -> B;
            case B -> C;
            case C -> D;
            case D -> D;
        };
    }
}
