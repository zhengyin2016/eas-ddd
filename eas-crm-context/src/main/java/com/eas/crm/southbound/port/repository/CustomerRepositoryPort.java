package com.eas.crm.southbound.port.repository;

import com.eas.crm.domain.customer.Customer;
import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.customer.CustomerLevel;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(CustomerId id);
    List<Customer> findByLevel(CustomerLevel level);
    List<Customer> findByCreatorId(String creatorId);
    List<Customer> findAll();
    void delete(CustomerId id);
    boolean existsByName(String name);
}
