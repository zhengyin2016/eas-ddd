package com.eas.crm.domain.contract;

import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.opportunity.Money;
import com.eas.crm.domain.opportunity.OpportunityId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Contract {
    private final ContractId id;
    private final CustomerId customerId;
    private final OpportunityId opportunityId;
    private String title;
    private Money amount;
    private ContractStatus status;
    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Money paidAmount;
    private final List<PaymentPlan> paymentPlans;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String approverId;
    private String rejectReason;
    private String terminateReason;

    private Contract(ContractId id, CustomerId customerId, OpportunityId opportunityId,
                    String title, Money amount, ContractStatus status,
                    LocalDate signDate, LocalDate startDate, LocalDate endDate,
                    Money paidAmount, List<PaymentPlan> paymentPlans,
                    LocalDateTime createdAt, LocalDateTime updatedAt,
                    String approverId, String rejectReason, String terminateReason) {
        this.id = Objects.requireNonNull(id, "Contract ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.opportunityId = opportunityId;
        this.title = title;
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.signDate = signDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paidAmount = paidAmount != null ? paidAmount : Money.zero();
        this.paymentPlans = new ArrayList<>(paymentPlans != null ? paymentPlans : List.of());
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        this.approverId = approverId;
        this.rejectReason = rejectReason;
        this.terminateReason = terminateReason;
        validate();
    }

    public static Contract create(CustomerId customerId, OpportunityId opportunityId,
                                 String title, Money amount, LocalDate signDate,
                                 LocalDate startDate, LocalDate endDate) {
        LocalDateTime now = LocalDateTime.now();
        Contract contract = new Contract(
                ContractId.generate(),
                customerId,
                opportunityId,
                title,
                amount,
                ContractStatus.DRAFT,
                signDate,
                startDate,
                endDate,
                Money.zero(),
                new ArrayList<>(),
                now,
                now,
                null,
                null,
                null
        );
        return contract;
    }

    public static Contract restore(ContractId id, CustomerId customerId, OpportunityId opportunityId,
                                  String title, Money amount, ContractStatus status,
                                  LocalDate signDate, LocalDate startDate, LocalDate endDate,
                                  Money paidAmount, List<PaymentPlan> paymentPlans,
                                  LocalDateTime createdAt, LocalDateTime updatedAt,
                                  String approverId, String rejectReason, String terminateReason) {
        return new Contract(id, customerId, opportunityId, title, amount, status,
                signDate, startDate, endDate, paidAmount, paymentPlans,
                createdAt, updatedAt, approverId, rejectReason, terminateReason);
    }

    public void submitForReview() {
        validateStatusTransition(ContractStatus.UNDER_REVIEW);
        this.status = ContractStatus.UNDER_REVIEW;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve(String approverId) {
        validateStatusTransition(ContractStatus.APPROVED);
        this.status = ContractStatus.APPROVED;
        this.approverId = approverId;
        this.rejectReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        validateStatusTransition(ContractStatus.DRAFT);
        this.status = ContractStatus.DRAFT;
        this.rejectReason = reason;
        this.approverId = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        validateStatusTransition(ContractStatus.ACTIVE);
        validatePaymentPlans();
        this.status = ContractStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        validateStatusTransition(ContractStatus.COMPLETED);
        if (!isFullyPaid()) {
            throw new IllegalStateException("Cannot complete contract: not fully paid");
        }
        this.status = ContractStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void terminate(String reason) {
        validateStatusTransition(ContractStatus.TERMINATED);
        this.status = ContractStatus.TERMINATED;
        this.terminateReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void addPaymentPlan(PaymentPlan plan) {
        if (this.status != ContractStatus.DRAFT) {
            throw new IllegalStateException("Payment plans can only be added to draft contracts");
        }
        this.paymentPlans.add(plan);
        validatePaymentPlans();
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePaidAmount(Money amount) {
        this.paidAmount = this.paidAmount.add(amount);
        if (isFullyPaid() && this.status == ContractStatus.ACTIVE) {
            this.status = ContractStatus.COMPLETED;
        }
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Contract title cannot be null or blank");
        }
        if (amount.isZero()) {
            throw new IllegalArgumentException("Contract amount must be greater than zero");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        validatePaymentPlans();
    }

    private void validateStatusTransition(ContractStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s",
                            this.status, newStatus));
        }
    }

    private void validatePaymentPlans() {
        if (!paymentPlans.isEmpty()) {
            Money totalPlanAmount = paymentPlans.stream()
                    .map(PaymentPlan::getAmount)
                    .reduce(Money.zero(), Money::add);
            if (!totalPlanAmount.equals(this.amount)) {
                throw new IllegalStateException(
                        "Total payment plan amount must equal contract amount");
            }
        }
    }

    public boolean isFullyPaid() {
        return paidAmount.equals(amount);
    }

    public Money getUnpaidAmount() {
        return amount.subtract(paidAmount);
    }

    // Getters
    public ContractId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public OpportunityId getOpportunityId() {
        return opportunityId;
    }

    public String getTitle() {
        return title;
    }

    public Money getAmount() {
        return amount;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public LocalDate getSignDate() {
        return signDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Money getPaidAmount() {
        return paidAmount;
    }

    public List<PaymentPlan> getPaymentPlans() {
        return new ArrayList<>(paymentPlans);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getApproverId() {
        return approverId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public String getTerminateReason() {
        return terminateReason;
    }
}
