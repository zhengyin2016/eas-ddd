package com.eas.crm.domain.opportunity;

import com.eas.crm.domain.customer.CustomerId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Opportunity {
    private final OpportunityId id;
    private final CustomerId customerId;
    private String title;
    private Money estimatedAmount;
    private OpportunityStage stage;
    private int probability;
    private LocalDate expectedCloseDate;
    private final String ownerId;
    private boolean isWon;
    private boolean isLost;
    private String lostReason;
    private Money actualAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Opportunity(OpportunityId id, CustomerId customerId, String title,
                        Money estimatedAmount, OpportunityStage stage, int probability,
                        LocalDate expectedCloseDate, String ownerId, boolean isWon,
                        boolean isLost, String lostReason, Money actualAmount,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Opportunity ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.title = title;
        this.estimatedAmount = Objects.requireNonNull(estimatedAmount, "Estimated amount cannot be null");
        this.stage = Objects.requireNonNull(stage, "Stage cannot be null");
        this.probability = probability;
        this.expectedCloseDate = expectedCloseDate;
        this.ownerId = Objects.requireNonNull(ownerId, "Owner ID cannot be null");
        this.isWon = isWon;
        this.isLost = isLost;
        this.lostReason = lostReason;
        this.actualAmount = actualAmount;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        validate();
    }

    public static Opportunity create(CustomerId customerId, String title,
                                     Money estimatedAmount, LocalDate expectedCloseDate,
                                     String ownerId) {
        LocalDateTime now = LocalDateTime.now();
        Opportunity opp = new Opportunity(
                OpportunityId.generate(),
                customerId,
                title,
                estimatedAmount,
                OpportunityStage.INITIAL_CONTACT,
                OpportunityStage.INITIAL_CONTACT.getProbability(),
                expectedCloseDate,
                ownerId,
                false,
                false,
                null,
                null,
                now,
                now
        );
        return opp;
    }

    public static Opportunity restore(OpportunityId id, CustomerId customerId, String title,
                                     Money estimatedAmount, OpportunityStage stage, int probability,
                                     LocalDate expectedCloseDate, String ownerId, boolean isWon,
                                     boolean isLost, String lostReason, Money actualAmount,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Opportunity(id, customerId, title, estimatedAmount, stage, probability,
                expectedCloseDate, ownerId, isWon, isLost, lostReason, actualAmount,
                createdAt, updatedAt);
    }

    public void advanceStage(OpportunityStage newStage) {
        validateNotClosed();
        validateStageTransition(newStage);

        this.stage = newStage;
        this.probability = newStage.getProbability();
        this.updatedAt = LocalDateTime.now();

        // 如果到达合同签订阶段，自动标记为赢单
        if (newStage == OpportunityStage.CONTRACT_SIGNING) {
            this.isWon = true;
        }
    }

    public void markWon(Money actualAmount) {
        validateNotClosed();
        this.isWon = true;
        this.actualAmount = Objects.requireNonNull(actualAmount, "Actual amount cannot be null");
        this.stage = OpportunityStage.CONTRACT_SIGNING;
        this.probability = 100;
        this.updatedAt = LocalDateTime.now();
    }

    public void markLost(String reason) {
        validateNotClosed();
        this.isLost = true;
        this.lostReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateExpectedCloseDate(LocalDate newDate) {
        validateNotClosed();
        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expected close date cannot be in the past");
        }
        this.expectedCloseDate = newDate;
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Opportunity title cannot be null or blank");
        }
        if (estimatedAmount.isZero()) {
            throw new IllegalArgumentException("Estimated amount must be greater than zero");
        }
        if (expectedCloseDate != null && expectedCloseDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expected close date cannot be in the past");
        }
        if (isWon && isLost) {
            throw new IllegalStateException("Opportunity cannot be both won and lost");
        }
    }

    private void validateNotClosed() {
        if (isWon) {
            throw new IllegalStateException("Cannot modify a won opportunity");
        }
        if (isLost) {
            throw new IllegalStateException("Cannot modify a lost opportunity");
        }
    }

    private void validateStageTransition(OpportunityStage newStage) {
        if (!newStage.isAfter(this.stage)) {
            throw new IllegalArgumentException(
                    String.format("Cannot advance from %s to %s. Stage can only move forward.",
                            this.stage, newStage));
        }
    }

    // Getters
    public OpportunityId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getTitle() {
        return title;
    }

    public Money getEstimatedAmount() {
        return estimatedAmount;
    }

    public OpportunityStage getStage() {
        return stage;
    }

    public int getProbability() {
        return probability;
    }

    public LocalDate getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public boolean isWon() {
        return isWon;
    }

    public boolean isLost() {
        return isLost;
    }

    public String getLostReason() {
        return lostReason;
    }

    public Money getActualAmount() {
        return actualAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
