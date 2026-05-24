package com.eas.crm.domain.payment;

import com.eas.crm.domain.contract.ContractId;
import com.eas.crm.domain.opportunity.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private final PaymentId id;
    private final ContractId contractId;
    private Money amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String remark;
    private final LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

    private Payment(PaymentId id, ContractId contractId, Money amount,
                   LocalDate paymentDate, PaymentMethod paymentMethod,
                   PaymentStatus status, String remark, LocalDateTime createdAt,
                   LocalDateTime confirmedAt) {
        this.id = Objects.requireNonNull(id, "Payment ID cannot be null");
        this.contractId = Objects.requireNonNull(contractId, "Contract ID cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.remark = remark;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.confirmedAt = confirmedAt;
        validate();
    }

    public static Payment create(ContractId contractId, Money amount,
                                LocalDate paymentDate, PaymentMethod paymentMethod,
                                String remark) {
        return new Payment(
                PaymentId.generate(),
                contractId,
                amount,
                paymentDate,
                paymentMethod,
                PaymentStatus.PLANNED,
                remark,
                LocalDateTime.now(),
                null
        );
    }

    public static Payment restore(PaymentId id, ContractId contractId, Money amount,
                                 LocalDate paymentDate, PaymentMethod paymentMethod,
                                 PaymentStatus status, String remark,
                                 LocalDateTime createdAt, LocalDateTime confirmedAt) {
        return new Payment(id, contractId, amount, paymentDate, paymentMethod,
                status, remark, createdAt, confirmedAt);
    }

    public void confirm() {
        validateStatusTransition(PaymentStatus.PAID);
        this.status = PaymentStatus.PAID;
        this.confirmedAt = LocalDateTime.now();
    }

    public void markOverdue() {
        validateStatusTransition(PaymentStatus.OVERDUE);
        this.status = PaymentStatus.OVERDUE;
    }

    public void cancel(String reason) {
        validateStatusTransition(PaymentStatus.CANCELLED);
        this.status = PaymentStatus.CANCELLED;
        this.remark = reason;
    }

    private void validate() {
        if (amount.isZero()) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (paymentDate != null && paymentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Payment date cannot be in the future");
        }
    }

    private void validateStatusTransition(PaymentStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s",
                            this.status, newStatus));
        }
    }

    // Getters
    public PaymentId getId() {
        return id;
    }

    public ContractId getContractId() {
        return contractId;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
}
