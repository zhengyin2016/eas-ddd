package com.eas.crm.domain.contract;

import com.eas.crm.domain.opportunity.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentPlan {
    private final PaymentPlanId id;
    private final ContractId contractId;
    private Money amount;
    private LocalDate dueDate;
    private PaymentPlanStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;

    private PaymentPlan(PaymentPlanId id, ContractId contractId, Money amount,
                       LocalDate dueDate, PaymentPlanStatus status, LocalDateTime createdAt,
                       LocalDateTime paidAt) {
        this.id = Objects.requireNonNull(id, "Payment plan ID cannot be null");
        this.contractId = Objects.requireNonNull(contractId, "Contract ID cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.dueDate = dueDate;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.paidAt = paidAt;
        validate();
    }

    public static PaymentPlan create(ContractId contractId, Money amount, LocalDate dueDate) {
        return new PaymentPlan(
                PaymentPlanId.generate(),
                contractId,
                amount,
                dueDate,
                PaymentPlanStatus.PENDING,
                LocalDateTime.now(),
                null
        );
    }

    public static PaymentPlan restore(PaymentPlanId id, ContractId contractId, Money amount,
                                     LocalDate dueDate, PaymentPlanStatus status,
                                     LocalDateTime createdAt, LocalDateTime paidAt) {
        return new PaymentPlan(id, contractId, amount, dueDate, status, createdAt, paidAt);
    }

    public void markPaid() {
        if (this.status == PaymentPlanStatus.PAID) {
            throw new IllegalStateException("Payment plan is already paid");
        }
        this.status = PaymentPlanStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markOverdue() {
        if (this.status == PaymentPlanStatus.PAID) {
            throw new IllegalStateException("Cannot mark paid payment plan as overdue");
        }
        if (this.status != PaymentPlanStatus.OVERDUE) {
            this.status = PaymentPlanStatus.OVERDUE;
        }
    }

    private void validate() {
        if (amount.isZero()) {
            throw new IllegalArgumentException("Payment plan amount must be greater than zero");
        }
    }

    public boolean isOverdue() {
        return status == PaymentPlanStatus.PENDING && LocalDate.now().isAfter(dueDate);
    }

    // Getters
    public PaymentPlanId getId() {
        return id;
    }

    public ContractId getContractId() {
        return contractId;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public PaymentPlanStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}
