package com.eas.crm.southbound.adapter.repository;

import com.eas.crm.domain.contract.ContractId;
import com.eas.crm.domain.payment.*;
import com.eas.crm.southbound.mapper.PaymentMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentMapper paymentMapper;

    public PaymentRepositoryImpl(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Payment save(Payment payment) {
        boolean exists = paymentMapper.existsById(payment.getId().value());

        if (exists) {
            paymentMapper.update(payment);
        } else {
            paymentMapper.insert(payment);
        }

        return payment;
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return paymentMapper.findById(id.value())
                .map(this::restorePayment);
    }

    @Override
    public List<Payment> findByContractId(ContractId contractId) {
        return paymentMapper.findByContractId(contractId.value()).stream()
                .map(this::restorePayment)
                .collect(Collectors.toList());
    }

    @Override
    public Money sumPaidAmountByContract(ContractId contractId) {
        BigDecimal sum = paymentMapper.sumPaidAmountByContract(contractId.value());
        return sum != null ? Money.of(sum) : Money.zero();
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentMapper.findByStatus(status.name()).stream()
                .map(this::restorePayment)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return paymentMapper.findAll().stream()
                .map(this::restorePayment)
                .collect(Collectors.toList());
    }

    private Payment restorePayment(PaymentMapper.PaymentDO paymentDO) {
        return Payment.restore(
                PaymentId.of(paymentDO.id()),
                ContractId.of(paymentDO.contractId()),
                Money.of(paymentDO.amount()),
                paymentDO.paymentDate(),
                PaymentMethod.valueOf(paymentDO.paymentMethod()),
                PaymentStatus.valueOf(paymentDO.status()),
                paymentDO.remark(),
                paymentDO.createdAt(),
                paymentDO.confirmedAt()
        );
    }
}
