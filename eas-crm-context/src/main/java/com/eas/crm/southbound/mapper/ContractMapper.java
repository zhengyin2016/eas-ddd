package com.eas.crm.southbound.mapper;

import com.eas.crm.domain.contract.Contract;
import com.eas.crm.domain.contract.ContractId;
import com.eas.crm.domain.contract.PaymentPlan;
import com.eas.crm.domain.opportunity.Money;
import com.eas.crm.domain.opportunity.OpportunityId;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface ContractMapper {

    @Insert("""
            INSERT INTO contract (id, customer_id, opportunity_id, title, amount, status, sign_date,
                                start_date, end_date, paid_amount, created_at, updated_at)
            VALUES (#{id}, #{customerId}, #{opportunityId}, #{title}, #{amount}, #{status}, #{signDate},
                    #{startDate}, #{endDate}, #{paidAmount}, #{createdAt}, #{updatedAt})
            """)
    void insert(Contract contract);

    @Update("""
            UPDATE contract
            SET status = #{status}, paid_amount = #{paidAmount}, approver_id = #{approverId},
                reject_reason = #{rejectReason}, terminate_reason = #{terminateReason},
                updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    void update(Contract contract);

    @Select("SELECT * FROM contract WHERE id = #{id}")
    @Results(id = "contractResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "opportunityId", column = "opportunity_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "signDate", column = "sign_date"),
            @Result(property = "startDate", column = "start_date"),
            @Result(property = "endDate", column = "end_date"),
            @Result(property = "paidAmount", column = "paid_amount"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "approverId", column = "approver_id"),
            @Result(property = "rejectReason", column = "reject_reason"),
            @Result(property = "terminateReason", column = "terminate_reason")
    })
    ContractDO findById(String id);

    @Select("SELECT * FROM contract WHERE customer_id = #{customerId}")
    List<ContractDO> findByCustomerId(String customerId);

    @Select("SELECT * FROM contract WHERE status = #{status}")
    List<ContractDO> findByStatus(String status);

    @Select("SELECT * FROM contract")
    List<ContractDO> findAll();

    @Select("SELECT id FROM contract WHERE id = #{id}")
    boolean existsById(String id);

    @Insert("""
            INSERT INTO payment_plan (id, contract_id, amount, due_date, status, created_at, paid_at)
            VALUES (#{id}, #{contractId}, #{amount}, #{dueDate}, #{status}, #{createdAt}, #{paidAt})
            """)
    void insertPaymentPlan(PaymentPlan plan);

    @Delete("DELETE FROM payment_plan WHERE contract_id = #{contractId}")
    void deletePaymentPlans(String contractId);

    @Select("SELECT * FROM payment_plan WHERE contract_id = #{contractId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "contractId", column = "contract_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "dueDate", column = "due_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "paidAt", column = "paid_at")
    })
    List<PaymentPlanDO> findPaymentPlansByContractId(String contractId);

    record ContractDO(
            String id,
            String customerId,
            String opportunityId,
            String title,
            BigDecimal amount,
            String status,
            LocalDate signDate,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal paidAmount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String approverId,
            String rejectReason,
            String terminateReason
    ) {}

    record PaymentPlanDO(
            String id,
            String contractId,
            BigDecimal amount,
            LocalDate dueDate,
            String status,
            LocalDateTime createdAt,
            LocalDateTime paidAt
    ) {}
}
