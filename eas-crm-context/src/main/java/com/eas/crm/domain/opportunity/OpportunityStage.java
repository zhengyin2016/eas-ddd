package com.eas.crm.domain.opportunity;

public enum OpportunityStage {
    INITIAL_CONTACT("初步接触", 20),
    NEEDS_CONFIRMATION("需求确认", 40),
    PROPOSAL("方案报价", 60),
    NEGOTIATION("商务谈判", 80),
    CONTRACT_SIGNING("合同签订", 95);

    private final String description;
    private final int probability;

    OpportunityStage(String description, int probability) {
        this.description = description;
        this.probability = probability;
    }

    public String getDescription() {
        return description;
    }

    public int getProbability() {
        return probability;
    }

    public boolean isAfter(OpportunityStage other) {
        return this.ordinal() > other.ordinal();
    }
}
