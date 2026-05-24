package com.eas.crm.northbound.appservice;

import com.eas.crm.domain.contract.*;
import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.customer.CustomerRepository;
import com.eas.crm.domain.opportunity.*;
import com.eas.crm.message.ContractResponse;
import com.eas.crm.message.CreateContractRequest;
import com.eas.crm.northbound.appservice.salesanalysis.SalesAnalysisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContractAppService {

    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;
    private final SalesAnalysisService salesAnalysisService;

    public ContractAppService(ContractRepository contractRepository,
                             CustomerRepository customerRepository,
                             OpportunityRepository opportunityRepository,
                             SalesAnalysisService salesAnalysisService) {
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
        this.opportunityRepository = opportunityRepository;
        this.salesAnalysisService = salesAnalysisService;
    }

    @Transactional
    public ContractResponse createContract(CreateContractRequest request) {
        // 验证客户存在
        if (customerRepository.findById(CustomerId.of(request.customerId())).isEmpty()) {
            throw new IllegalArgumentException("Customer not found: " + request.customerId());
        }

        Contract contract = Contract.create(
                CustomerId.of(request.customerId()),
                request.opportunityId() != null ? OpportunityId.of(request.opportunityId()) : null,
                request.title(),
                Money.of(request.amount()),
                request.signDate(),
                request.startDate(),
                request.endDate()
        );

        // 如果关联了商机，标记商机为赢单
        if (request.opportunityId() != null) {
            opportunityRepository.findById(OpportunityId.of(request.opportunityId()))
                    .ifPresent(opportunity -> {
                        opportunity.markWon(Money.of(request.amount()));
                        opportunityRepository.save(opportunity);
                    });
        }

        Contract saved = contractRepository.save(contract);
        return toResponse(saved);
    }

    @Transactional
    public ContractResponse submitForReview(String contractId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        contract.submitForReview();
        Contract updated = contractRepository.save(contract);
        return toResponse(updated);
    }

    @Transactional
    public ContractResponse approve(String contractId, String approverId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        contract.approve(approverId);
        Contract updated = contractRepository.save(contract);
        return toResponse(updated);
    }

    @Transactional
    public ContractResponse reject(String contractId, String reason) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        contract.reject(reason);
        Contract updated = contractRepository.save(contract);
        return toResponse(updated);
    }

    @Transactional
    public ContractResponse activate(String contractId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        contract.activate();

        // 通知PM上下文创建项目（模拟）
        // pmClientPort.createProject(...)

        Contract updated = contractRepository.save(contract);
        return toResponse(updated);
    }

    @Transactional
    public ContractResponse complete(String contractId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        contract.complete();
        Contract updated = contractRepository.save(contract);
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public ContractResponse getContract(String contractId) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));
        return toResponse(contract);
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> findByCustomerId(String customerId) {
        return contractRepository.findByCustomerId(CustomerId.of(customerId)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createPaymentPlan(String contractId, List<PaymentPlanRequest> plans) {
        Contract contract = contractRepository.findById(ContractId.of(contractId))
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        for (PaymentPlanRequest plan : plans) {
            PaymentPlan paymentPlan = PaymentPlan.create(
                    contract.getId(),
                    Money.of(plan.amount()),
                    plan.dueDate()
            );
            contract.addPaymentPlan(paymentPlan);
        }

        contractRepository.save(contract);
    }

    public record PaymentPlanRequest(BigDecimal amount, java.time.LocalDate dueDate) {}

    private ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
                contract.getId().value(),
                contract.getCustomerId().value(),
                contract.getOpportunityId() != null ? contract.getOpportunityId().value() : null,
                contract.getTitle(),
                new BigDecimal(contract.getAmount().amount().toString()),
                contract.getStatus(),
                contract.getSignDate(),
                contract.getStartDate(),
                contract.getEndDate(),
                new BigDecimal(contract.getPaidAmount().amount().toString()),
                new BigDecimal(contract.getUnpaidAmount().amount().toString()),
                contract.getCreatedAt(),
                contract.getUpdatedAt(),
                contract.getApproverId(),
                contract.getRejectReason(),
                contract.getPaymentPlans().stream()
                        .map(p -> new ContractResponse.PaymentPlanInfo(
                                p.getId().value(),
                                new BigDecimal(p.getAmount().amount().toString()),
                                p.getDueDate(),
                                p.getStatus().name(),
                                p.getPaidAt()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
