package com.eas.crm.domain.payment;

import com.eas.crm.domain.contract.ContractId;
import com.eas.crm.domain.opportunity.Money;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    List<Payment> findByContractId(ContractId contractId);
    Money sumPaidAmountByContract(ContractId contractId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findAll();
}
