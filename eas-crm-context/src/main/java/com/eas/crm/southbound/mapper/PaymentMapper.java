package com.eas.crm.southbound.mapper;

import com.eas.crm.domain.payment.Payment;
import com.eas.crm.domain.payment.PaymentId;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface PaymentMapper {

    @Insert("""
            INSERT INTO payment (id, contract_id, amount, payment_date, payment_method, status, remark, created_at, confirmed_at)
            VALUES (#{id}, #{contractId}, #{amount}, #{paymentDate}, #{paymentMethod}, #{status}, #{remark}, #{createdAt}, #{confirmedAt})
            """)
    void insert(Payment payment);

    @Update("""
            UPDATE payment
            SET status = #{status}, confirmed_at = #{confirmedAt}, remark = #{remark}
            WHERE id = #{id}
            """)
    void update(Payment payment);

    @Select("SELECT * FROM payment WHERE id = #{id}")
    @Results(id = "paymentResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "contractId", column = "contract_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "paymentDate", column = "payment_date"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "status", column = "status"),
            @Result(property = "remark", column = "remark"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "confirmedAt", column = "confirmed_at")
    })
    PaymentDO findById(String id);

    @Select("SELECT * FROM payment WHERE contract_id = #{contractId}")
    List<PaymentDO> findByContractId(String contractId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM payment WHERE contract_id = #{contractId} AND status = 'PAID'")
    BigDecimal sumPaidAmountByContract(String contractId);

    @Select("SELECT * FROM payment WHERE status = #{status}")
    List<PaymentDO> findByStatus(String status);

    @Select("SELECT * FROM payment")
    List<PaymentDO> findAll();

    @Select("SELECT id FROM payment WHERE id = #{id}")
    boolean existsById(String id);

    record PaymentDO(
            String id,
            String contractId,
            BigDecimal amount,
            LocalDate paymentDate,
            String paymentMethod,
            String status,
            String remark,
            LocalDateTime createdAt,
            LocalDateTime confirmedAt
    ) {}
}
