package com.eas.crm.domain.customer;

import java.util.regex.Pattern;

public class Contact {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final ContactId id;
    private String name;
    private String phone;
    private String email;
    private String position;
    private boolean isPrimary;

    public Contact(ContactId id, String name, String phone, String email, String position, boolean isPrimary) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.isPrimary = isPrimary;
        validate();
    }

    public static Contact createPrimary(String name, String phone, String email, String position) {
        return new Contact(ContactId.generate(), name, phone, email, position, true);
    }

    public static Contact createSecondary(String name, String phone, String email, String position) {
        return new Contact(ContactId.generate(), name, phone, email, position, false);
    }

    public void update(String name, String phone, String email, String position) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        validate();
    }

    public void setPrimary() {
        this.isPrimary = true;
    }

    public void unsetPrimary() {
        this.isPrimary = false;
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Contact name cannot be null or blank");
        }
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public ContactId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public boolean isPrimary() {
        return isPrimary;
    }
}
