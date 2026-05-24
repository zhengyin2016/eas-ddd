package com.eas.crm.domain.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("客户聚合测试")
class CustomerTest {

    @Test
    @DisplayName("应该成功创建客户，默认等级为D级")
    void shouldCreateCustomerWithDefaultLevelD() {
        // Given
        String name = "测试公司";
        String industry = "软件开发";
        CustomerSource source = CustomerSource.REFERRAL;
        String contactName = "张三";
        String contactPhone = "13800138000";
        String creatorId = "user001";

        // When
        Customer customer = Customer.create(
                name, industry, source, contactName, contactPhone, null, creatorId
        );

        // Then
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getIndustry()).isEqualTo(industry);
        assertThat(customer.getLevel()).isEqualTo(CustomerLevel.D);
        assertThat(customer.getSource()).isEqualTo(source);
        assertThat(customer.getContacts()).hasSize(1);
        assertThat(customer.getContacts().get(0).isPrimary()).isTrue();
    }

    @Test
    @DisplayName("应该成功升级客户等级")
    void shouldUpgradeCustomerLevel() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );

        // When
        customer.upgradeLevel();

        // Then
        assertThat(customer.getLevel()).isEqualTo(CustomerLevel.C);
    }

    @Test
    @DisplayName("A级客户不能再升级")
    void shouldNotUpgradeALevelCustomer() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );
        customer.setLevel(CustomerLevel.A);

        // When
        customer.upgradeLevel();

        // Then
        assertThat(customer.getLevel()).isEqualTo(CustomerLevel.A);
    }

    @Test
    @DisplayName("应该成功降级客户等级")
    void shouldDowngradeCustomerLevel() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );
        customer.setLevel(CustomerLevel.B);

        // When
        customer.downgradeLevel();

        // Then
        assertThat(customer.getLevel()).isEqualTo(CustomerLevel.C);
    }

    @Test
    @DisplayName("D级客户不能再降级")
    void shouldNotDowngradeDLevelCustomer() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );

        // When
        customer.downgradeLevel();

        // Then
        assertThat(customer.getLevel()).isEqualTo(CustomerLevel.D);
    }

    @Test
    @DisplayName("应该成功添加联系人")
    void shouldAddContact() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );

        // When
        Contact contact = Contact.createSecondary("李四", "13900139000", "lisi@example.com", "技术总监");
        customer.addContact(contact);

        // Then
        assertThat(customer.getContacts()).hasSize(2);
    }

    @Test
    @DisplayName("应该成功设置主要联系人")
    void shouldSetPrimaryContact() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );
        Contact newContact = Contact.createSecondary("李四", "13900139000", null, null);
        customer.addContact(newContact);

        // When
        customer.setPrimaryContact(newContact.getId());

        // Then
        assertThat(customer.getContacts().get(0).isPrimary()).isFalse();
        assertThat(customer.getContacts().get(1).isPrimary()).isTrue();
    }

    @Test
    @DisplayName("创建客户时名称不能为空")
    void shouldNotCreateCustomerWithEmptyName() {
        // When & Then
        assertThatThrownBy(() -> Customer.create(
                "", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("联系人不为空时自动设为主要联系人")
    void shouldSetFirstContactAsPrimary() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );

        // When & Then
        assertThat(customer.getContacts()).hasSize(1);
        assertThat(customer.getContacts().get(0).isPrimary()).isTrue();
    }

    @Test
    @DisplayName("应该成功更新客户信息")
    void shouldUpdateCustomerInfo() {
        // Given
        Customer customer = Customer.create(
                "测试公司", "软件开发", CustomerSource.REFERRAL,
                "张三", "13800138000", null, "user001"
        );

        // When
        customer.update("新公司名", "互联网", "北京市朝阳区");

        // Then
        assertThat(customer.getName()).isEqualTo("新公司名");
        assertThat(customer.getIndustry()).isEqualTo("互联网");
        assertThat(customer.getAddress()).isEqualTo("北京市朝阳区");
    }
}
