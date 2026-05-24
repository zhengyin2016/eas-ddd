package com.eas.crm.domain.contract;

import com.eas.crm.domain.customer.CustomerId;

import java.util.List;
import java.util.Optional;

public interface ContractRepository {
    Contract save(Contract contract);
    Optional<Contract> findById(ContractId id);
    List<Contract> findByCustomerId(CustomerId customerId);
    List<Contract> findByStatus(ContractStatus status);
    List<Contract> findAll();
}
