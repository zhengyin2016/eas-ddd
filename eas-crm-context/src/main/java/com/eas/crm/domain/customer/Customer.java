package com.eas.crm.domain.customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Customer {
    private final CustomerId id;
    private String name;
    private String industry;
    private CustomerLevel level;
    private final CustomerSource source;
    private String contactName;
    private String contactPhone;
    private String address;
    private final String creatorId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<Contact> contacts;

    private Customer(CustomerId id, String name, String industry, CustomerLevel level,
                     CustomerSource source, String contactName, String contactPhone,
                     String address, String creatorId, LocalDateTime createdAt,
                     LocalDateTime updatedAt, List<Contact> contacts) {
        this.id = Objects.requireNonNull(id, "Customer ID cannot be null");
        this.name = name;
        this.industry = industry;
        this.level = Objects.requireNonNull(level, "Customer level cannot be null");
        this.source = Objects.requireNonNull(source, "Customer source cannot be null");
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.address = address;
        this.creatorId = Objects.requireNonNull(creatorId, "Creator ID cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        this.contacts = new ArrayList<>(contacts != null ? contacts : List.of());
        validate();
    }

    public static Customer create(String name, String industry, CustomerSource source,
                                  String contactName, String contactPhone, String address,
                                  String creatorId) {
        LocalDateTime now = LocalDateTime.now();
        Customer customer = new Customer(
                CustomerId.generate(),
                name,
                industry,
                CustomerLevel.D, // 新客户默认D级
                source,
                contactName,
                contactPhone,
                address,
                creatorId,
                now,
                now,
                new ArrayList<>()
        );

        // 添加主要联系人
        Contact primaryContact = Contact.createPrimary(contactName, contactPhone, null, null);
        customer.contacts.add(primaryContact);

        return customer;
    }

    public static Customer restore(CustomerId id, String name, String industry, CustomerLevel level,
                                   CustomerSource source, String contactName, String contactPhone,
                                   String address, String creatorId, LocalDateTime createdAt,
                                   LocalDateTime updatedAt, List<Contact> contacts) {
        return new Customer(id, name, industry, level, source, contactName, contactPhone,
                address, creatorId, createdAt, updatedAt, contacts);
    }

    public void update(String name, String industry, String address) {
        this.name = name;
        this.industry = industry;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void addContact(Contact contact) {
        validateContactLimit();
        this.contacts.add(contact);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContact(ContactId contactId, String name, String phone, String email, String position) {
        Contact contact = findContact(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));
        contact.update(name, phone, email, position);
        this.updatedAt = LocalDateTime.now();
    }

    public void setPrimaryContact(ContactId contactId) {
        // 先取消所有主要联系人
        this.contacts.forEach(Contact::unsetPrimary);
        // 设置新的主要联系人
        findContact(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId))
                .setPrimary();
        this.updatedAt = LocalDateTime.now();
    }

    public void upgradeLevel() {
        if (this.level != CustomerLevel.A) {
            this.level = this.level.nextLevel();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void downgradeLevel() {
        if (this.level != CustomerLevel.D) {
            this.level = this.level.previousLevel();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void setLevel(CustomerLevel newLevel) {
        this.level = Objects.requireNonNull(newLevel);
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or blank");
        }
        if (contacts.isEmpty()) {
            throw new IllegalArgumentException("Customer must have at least one contact");
        }
        ensurePrimaryContactExists();
    }

    private void validateContactLimit() {
        if (this.contacts.size() >= 20) {
            throw new IllegalStateException("Customer cannot have more than 20 contacts");
        }
    }

    private void ensurePrimaryContactExists() {
        if (contacts.stream().noneMatch(Contact::isPrimary)) {
            contacts.get(0).setPrimary();
        }
    }

    private Optional<Contact> findContact(ContactId contactId) {
        return contacts.stream()
                .filter(c -> c.getId().equals(contactId))
                .findFirst();
    }

    // Getters
    public CustomerId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIndustry() {
        return industry;
    }

    public CustomerLevel getLevel() {
        return level;
    }

    public CustomerSource getSource() {
        return source;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }
}
