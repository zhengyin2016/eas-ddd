package com.eas.crm.southbound.mapper;

import com.eas.crm.domain.opportunity.Opportunity;
import com.eas.crm.domain.opportunity.OpportunityId;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface OpportunityMapper {

    @Insert("""
            INSERT INTO opportunity (id, customer_id, title, estimated_amount, stage, probability,
                                   expected_close_date, owner_id, is_won, is_lost, lost_reason,
                                   actual_amount, created_at, updated_at)
            VALUES (#{id}, #{customerId}, #{title}, #{estimatedAmount}, #{stage}, #{probability},
                    #{expectedCloseDate}, #{ownerId}, #{isWon}, #{isLost}, #{lostReason},
                    #{actualAmount}, #{createdAt}, #{updatedAt})
            """)
    void insert(Opportunity opportunity);

    @Update("""
            UPDATE opportunity
            SET stage = #{stage}, probability = #{probability}, expected_close_date = #{expectedCloseDate},
                is_won = #{isWon}, is_lost = #{isLost}, lost_reason = #{lostReason},
                actual_amount = #{actualAmount}, updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    void update(Opportunity opportunity);

    @Select("SELECT * FROM opportunity WHERE id = #{id}")
    @Results(id = "opportunityResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "estimatedAmount", column = "estimated_amount"),
            @Result(property = "stage", column = "stage"),
            @Result(property = "probability", column = "probability"),
            @Result(property = "expectedCloseDate", column = "expected_close_date"),
            @Result(property = "ownerId", column = "owner_id"),
            @Result(property = "isWon", column = "is_won"),
            @Result(property = "isLost", column = "is_lost"),
            @Result(property = "lostReason", column = "lost_reason"),
            @Result(property = "actualAmount", column = "actual_amount"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    OpportunityDO findById(String id);

    @Select("SELECT * FROM opportunity WHERE customer_id = #{customerId}")
    List<OpportunityDO> findByCustomerId(String customerId);

    @Select("SELECT * FROM opportunity WHERE stage = #{stage}")
    List<OpportunityDO> findByStage(String stage);

    @Select("SELECT * FROM opportunity WHERE owner_id = #{ownerId}")
    List<OpportunityDO> findByOwnerId(String ownerId);

    @Select("SELECT * FROM opportunity")
    List<OpportunityDO> findAll();

    @Select("SELECT id FROM opportunity WHERE id = #{id}")
    boolean existsById(String id);

    record OpportunityDO(
            String id,
            String customerId,
            String title,
            BigDecimal estimatedAmount,
            String stage,
            int probability,
            LocalDate expectedCloseDate,
            String ownerId,
            boolean isWon,
            boolean isLost,
            String lostReason,
            BigDecimal actualAmount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
