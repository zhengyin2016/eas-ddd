package com.eas.crm.domain.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(CustomerId id);
    List<Customer> findByLevel(CustomerLevel level);
    List<Customer> findByCreatorId(String creatorId);
    List<Customer> findAll();
    void delete(CustomerId id);
    boolean existsByName(String name);
}
