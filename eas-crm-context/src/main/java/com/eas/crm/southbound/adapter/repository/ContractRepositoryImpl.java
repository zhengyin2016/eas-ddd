package com.eas.crm.southbound.adapter.repository;

import com.eas.crm.domain.contract.*;
import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.southbound.mapper.ContractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ContractRepositoryImpl implements ContractRepository {

    private final ContractMapper contractMapper;

    public ContractRepositoryImpl(ContractMapper contractMapper) {
        this.contractMapper = contractMapper;
    }

    @Override
    public Contract save(Contract contract) {
        boolean exists = contractMapper.existsById(contract.getId().value());

        if (exists) {
            contractMapper.update(contract);
            // 更新回款计划
            contractMapper.deletePaymentPlans(contract.getId().value());
            for (PaymentPlan plan : contract.getPaymentPlans()) {
                contractMapper.insertPaymentPlan(plan);
            }
        } else {
            contractMapper.insert(contract);
            // 插入回款计划
            for (PaymentPlan plan : contract.getPaymentPlans()) {
                contractMapper.insertPaymentPlan(plan);
            }
        }

        return contract;
    }

    @Override
    public Optional<Contract> findById(ContractId id) {
        return contractMapper.findById(id.value())
                .map(this::restoreContract);
    }

    @Override
    public List<Contract> findByCustomerId(CustomerId customerId) {
        return contractMapper.findByCustomerId(customerId.value()).stream()
                .map(this::restoreContract)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contract> findByStatus(ContractStatus status) {
        return contractMapper.findByStatus(status.name()).stream()
                .map(this::restoreContract)
                .collect(Collectors.toList());
    }

    @Override
    public List<Contract> findAll() {
        return contractMapper.findAll().stream()
                .map(this::restoreContract)
                .collect(Collectors.toList());
    }

    private Contract restoreContract(ContractMapper.ContractDO contractDO) {
        List<PaymentPlan> paymentPlans = contractMapper.findPaymentPlansByContractId(contractDO.id()).stream()
                .map(planDO -> PaymentPlan.restore(
                        PaymentPlanId.of(planDO.id()),
                        ContractId.of(planDO.contractId()),
                        planDO.amount() != null ? Money.of(planDO.amount()) : null,
                        planDO.dueDate(),
                        PaymentPlanStatus.valueOf(planDO.status()),
                        planDO.createdAt(),
                        planDO.paidAt()
                ))
                .collect(Collectors.toList());

        return Contract.restore(
                ContractId.of(contractDO.id()),
                CustomerId.of(contractDO.customerId()),
                contractDO.opportunityId() != null ? OpportunityId.of(contractDO.opportunityId()) : null,
                contractDO.title(),
                contractDO.amount() != null ? Money.of(contractDO.amount()) : null,
                ContractStatus.valueOf(contractDO.status()),
                contractDO.signDate(),
                contractDO.startDate(),
                contractDO.endDate(),
                contractDO.paidAmount() != null ? Money.of(contractDO.paidAmount()) : null,
                paymentPlans,
                contractDO.createdAt(),
                contractDO.updatedAt(),
                contractDO.approverId(),
                contractDO.rejectReason(),
                contractDO.terminateReason()
        );
    }
}
