package com.eas.crm.northbound.appservice;

import com.eas.crm.domain.contract.Contract;
import com.eas.crm.domain.contract.ContractId;
import com.eas.crm.domain.contract.ContractRepository;
import com.eas.crm.domain.contract.ContractStatus;
import com.eas.crm.domain.payment.*;
import com.eas.crm.message.PaymentResponse;
import com.eas.crm.message.RecordPaymentRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentAppService {

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;

    public PaymentAppService(PaymentRepository paymentRepository,
                            ContractRepository contractRepository) {
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional
    public PaymentResponse recordPayment(RecordPaymentRequest request) {
        Contract contract = contractRepository.findById(ContractId.of(request.contractId()))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + request.contractId()));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Payment can only be recorded for active contracts");
        }

        Payment payment = Payment.create(
                contract.getId(),
                Money.of(request.amount()),
                request.paymentDate(),
                request.paymentMethod(),
                request.remark()
        );

        Payment saved = paymentRepository.save(payment);

        // 更新合同的已回款金额
        contract.updatePaidAmount(Money.of(request.amount()));
        contractRepository.save(contract);

        return toResponse(saved);
    }

    @Transactional
    public PaymentResponse confirmPayment(String paymentId) {
        Payment payment = paymentRepository.findById(PaymentId.of(paymentId))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.confirm();
        Payment confirmed = paymentRepository.save(payment);

        // 回款确认后，更新对应的回款计划（如果存在）
        updatePaymentPlanStatus(payment);

        return toResponse(confirmed);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByContractId(String contractId) {
        return paymentRepository.findByContractId(ContractId.of(contractId)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentId) {
        Payment payment = paymentRepository.findById(PaymentId.of(paymentId))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        return toResponse(payment);
    }

    private void updatePaymentPlanStatus(Payment payment) {
        // 简化处理：找到对应的回款计划并标记为已支付
        // 实际应该有更复杂的匹配逻辑
        contractRepository.findById(payment.getContractId()).ifPresent(contract -> {
            contract.getPaymentPlans().stream()
                    .filter(p -> p.getStatus() == com.eas.crm.domain.contract.PaymentPlanStatus.PENDING)
                    .findFirst()
                    .ifPresent(plan -> {
                        plan.markPaid();
                        contractRepository.save(contract);
                    });
        });
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId().value(),
                payment.getContractId().value(),
                new BigDecimal(payment.getAmount().amount().toString()),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getRemark(),
                payment.getCreatedAt(),
                payment.getConfirmedAt()
        );
    }
}
