package com.eas.crm.southbound.adapter.repository;

import com.eas.crm.domain.customer.Contact;
import com.eas.crm.domain.customer.ContactId;
import com.eas.crm.domain.customer.Customer;
import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.customer.CustomerLevel;
import com.eas.crm.domain.customer.CustomerRepository;
import com.eas.crm.domain.customer.CustomerSource;
import com.eas.crm.southbound.mapper.CustomerMapper;
import com.eas.crm.southbound.port.repository.CustomerRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;
    private final CustomerRepositoryPort customerRepositoryPort;

    public CustomerRepositoryImpl(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
        this.customerRepositoryPort = customerMapper;
    }

    @Override
    public Customer save(Customer customer) {
        // 检查是新增还是更新
        boolean exists = customerMapper.existsById(customer.getId().value());

        if (exists) {
            customerMapper.update(customer);
            // 更新联系人
            customerMapper.deleteContacts(customer.getId().value());
            for (Contact contact : customer.getContacts()) {
                customerMapper.insertContact(customer.getId().value(), contact);
            }
        } else {
            customerMapper.insert(customer);
            // 插入联系人
            for (Contact contact : customer.getContacts()) {
                customerMapper.insertContact(customer.getId().value(), contact);
            }
        }

        return customer;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return customerMapper.findById(id.value())
                .map(this::restoreCustomer);
    }

    @Override
    public List<Customer> findByLevel(CustomerLevel level) {
        return customerMapper.findByLevel(level.name()).stream()
                .map(this::restoreCustomer)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findByCreatorId(String creatorId) {
        return customerMapper.findByCreatorId(creatorId).stream()
                .map(this::restoreCustomer)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findAll() {
        return customerMapper.findAll().stream()
                .map(this::restoreCustomer)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CustomerId id) {
        customerMapper.deleteById(id.value());
    }

    @Override
    public boolean existsByName(String name) {
        return customerMapper.existsByName(name);
    }

    private Customer restoreCustomer(CustomerMapper.CustomerDO customerDO) {
        List<Contact> contacts = customerMapper.findContactsByCustomerId(customerDO.id()).stream()
                .map(contactDO -> Contact.restore(
                        ContactId.of(contactDO.id()),
                        contactDO.name(),
                        contactDO.phone(),
                        contactDO.email(),
                        contactDO.position(),
                        contactDO.isPrimary()
                ))
                .collect(Collectors.toList());

        return Customer.restore(
                CustomerId.of(customerDO.id()),
                customerDO.name(),
                customerDO.industry(),
                CustomerLevel.valueOf(customerDO.level()),
                CustomerSource.valueOf(customerDO.source()),
                customerDO.contactName(),
                customerDO.contactPhone(),
                customerDO.address(),
                customerDO.creatorId(),
                customerDO.createdAt(),
                customerDO.updatedAt(),
                contacts
        );
    }
}
