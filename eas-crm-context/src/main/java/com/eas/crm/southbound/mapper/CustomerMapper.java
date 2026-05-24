package com.eas.crm.southbound.mapper;

import com.eas.crm.domain.customer.Contact;
import com.eas.crm.domain.customer.Customer;
import com.eas.crm.domain.customer.CustomerId;
import com.eas.crm.domain.customer.CustomerLevel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CustomerMapper {

    @Insert("""
            INSERT INTO customer (id, name, industry, level, source, contact_name, contact_phone, address, creator_id, created_at, updated_at)
            VALUES (#{id}, #{name}, #{industry}, #{level}, #{source}, #{contactName}, #{contactPhone}, #{address}, #{creatorId}, #{createdAt}, #{updatedAt})
            """)
    void insert(Customer customer);

    @Update("""
            UPDATE customer
            SET name = #{name}, industry = #{industry}, level = #{level}, address = #{address}, updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    void update(Customer customer);

    @Select("SELECT * FROM customer WHERE id = #{id}")
    @Results(id = "customerResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "industry", column = "industry"),
            @Result(property = "level", column = "level"),
            @Result(property = "source", column = "source"),
            @Result(property = "contactName", column = "contact_name"),
            @Result(property = "contactPhone", column = "contact_phone"),
            @Result(property = "address", column = "address"),
            @Result(property = "creatorId", column = "creator_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    CustomerDO findById(String id);

    @Select("SELECT * FROM customer WHERE level = #{level}")
    List<CustomerDO> findByLevel(String level);

    @Select("SELECT * FROM customer WHERE creator_id = #{creatorId}")
    List<CustomerDO> findByCreatorId(String creatorId);

    @Select("SELECT * FROM customer")
    List<CustomerDO> findAll();

    @Delete("DELETE FROM customer WHERE id = #{id}")
    void deleteById(String id);

    @Select("SELECT COUNT(*) FROM customer WHERE name = #{name}")
    boolean existsByName(String name);

    @Select("SELECT id FROM customer WHERE id = #{id}")
    boolean existsById(String id);

    @Insert("""
            INSERT INTO contact (id, customer_id, name, phone, email, position, is_primary)
            VALUES (#{id}, #{customerId}, #{name}, #{phone}, #{email}, #{position}, #{isPrimary})
            """)
    void insertContact(@Param("customerId") String customerId, Contact contact);

    @Delete("DELETE FROM contact WHERE customer_id = #{customerId}")
    void deleteContacts(String customerId);

    @Select("SELECT * FROM contact WHERE customer_id = #{customerId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "email", column = "email"),
            @Result(property = "position", column = "position"),
            @Result(property = "isPrimary", column = "is_primary")
    })
    List<ContactDO> findContactsByCustomerId(String customerId);

    record CustomerDO(
            String id,
            String name,
            String industry,
            String level,
            String source,
            String contactName,
            String contactPhone,
            String address,
            String creatorId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    record ContactDO(
            String id,
            String name,
            String phone,
            String email,
            String position,
            boolean isPrimary
    ) {}
}
