package com.eas.crm.northbound.appservice;

import com.eas.crm.domain.customer.Customer;
import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.customer.CustomerLevel;
import com.eas.crm.domain.customer.CustomerRepository;
import com.eas.crm.domain.customer.Contact;
import com.eas.crm.domain.customer.ContactId;
import com.eas.crm.message.CreateCustomerRequest;
import com.eas.crm.message.CustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerAppService {

    private final CustomerRepository customerRepository;

    public CustomerAppService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        // 检查客户名称是否已存在
        if (customerRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Customer already exists with name: " + request.name());
        }

        Customer customer = Customer.create(
                request.name(),
                request.industry(),
                request.source(),
                request.contactName(),
                request.contactPhone(),
                request.address(),
                request.creatorId()
        );

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    @Transactional
    public CustomerResponse updateCustomer(String customerId, String name, String industry, String address) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        customer.update(name, industry, address);
        Customer updated = customerRepository.save(customer);
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(String customerId) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findByLevel(CustomerLevel level) {
        return customerRepository.findByLevel(level).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void upgradeLevel(String customerId) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        customer.upgradeLevel();
        customerRepository.save(customer);
    }

    @Transactional
    public void downgradeLevel(String customerId) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        customer.downgradeLevel();
        customerRepository.save(customer);
    }

    @Transactional
    public void addContact(String customerId, String name, String phone, String email, String position) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Contact contact = Contact.createSecondary(name, phone, email, position);
        customer.addContact(contact);
        customerRepository.save(customer);
    }

    @Transactional
    public void setPrimaryContact(String customerId, String contactId) {
        Customer customer = customerRepository.findById(CustomerId.of(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        customer.setPrimaryContact(ContactId.of(contactId));
        customerRepository.save(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId().value(),
                customer.getName(),
                customer.getIndustry(),
                customer.getLevel(),
                customer.getSource(),
                customer.getContactName(),
                customer.getContactPhone(),
                customer.getAddress(),
                customer.getCreatorId(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.getContacts().stream()
                        .map(c -> new CustomerResponse.ContactInfo(
                                c.getId().value(),
                                c.getName(),
                                c.getPhone(),
                                c.getEmail(),
                                c.getPosition(),
                                c.isPrimary()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
