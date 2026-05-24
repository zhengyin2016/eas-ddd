package com.eas.hr.southbound.adapter.repository;

import com.eas.hr.domain.talent.*;
import com.eas.hr.domain.employee.Skill;
import com.eas.hr.domain.employee.SkillLevel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 储备人才MyBatis Mapper接口
 */
@Mapper
public interface TalentMapper {

    @Insert("INSERT INTO hr_talent (id, name, source, phone, email, wechat, status, notes, created_at, updated_at) " +
            "VALUES (#{id.value}, #{name}, #{source}, #{contactInfo.phone}, #{contactInfo.email}, " +
            "#{contactInfo.wechat}, #{status}, #{notes}, #{createdAt}, #{updatedAt})")
    void insert(Talent talent);

    @Update("UPDATE hr_talent SET name = #{name}, source = #{source}, phone = #{contactInfo.phone}, " +
            "email = #{contactInfo.email}, wechat = #{contactInfo.wechat}, status = #{status}, " +
            "notes = #{notes}, updated_at = #{updatedAt} WHERE id = #{id.value}")
    void update(Talent talent);

    @Select("SELECT id, name, source, phone, email, wechat, status, notes, created_at, updated_at " +
            "FROM hr_talent WHERE id = #{value}")
    @ResultMap("talentResultMap")
    TalentDO findById(String id);

    @Select("SELECT id, name, source, phone, email, wechat, status, notes, created_at, updated_at " +
            "FROM hr_talent WHERE status = #{value}")
    @ResultMap("talentResultMap")
    List<TalentDO> findByStatus(String status);

    @Select("SELECT id, name, source, phone, email, wechat, status, notes, created_at, updated_at " +
            "FROM hr_talent WHERE source = #{value}")
    @ResultMap("talentResultMap")
    List<TalentDO> findBySource(String source);

    @Select("SELECT id, name, source, phone, email, wechat, status, notes, created_at, updated_at " +
            "FROM hr_talent WHERE status = 'APPROVED' ORDER BY created_at")
    @ResultMap("talentResultMap")
    List<TalentDO> findApprovedForConversion();

    @Insert("INSERT INTO hr_talent_skill (talent_id, skill_name, skill_level, certified_date) " +
            "VALUES (#{talentId}, #{skillName}, #{level}, #{certifiedDate})")
    void insertSkill(@Param("talentId") String talentId,
                     @Param("skillName") String skillName,
                     @Param("level") String level,
                     @Param("certifiedDate") LocalDate certifiedDate);

    @Delete("DELETE FROM hr_talent_skill WHERE talent_id = #{talentId}")
    void deleteSkills(@Param("talentId") String talentId);

    @Select("SELECT skill_name, skill_level, certified_date FROM hr_talent_skill WHERE talent_id = #{value}")
    @Results({
            @Result(property = "name", column = "skill_name"),
            @Result(property = "level", column = "skill_level", javaType = SkillLevel.class),
            @Result(property = "certifiedDate", column = "certified_date")
    })
    List<Skill> findSkillsByTalentId(String talentId);

    /**
     * 储备人才数据对象
     */
    class TalentDO {
        private String id;
        private String name;
        private TalentSource source;
        private String phone;
        private String email;
        private String wechat;
        private TalentStatus status;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public TalentSource getSource() { return source; }
        public void setSource(TalentSource source) { this.source = source; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getWechat() { return wechat; }
        public void setWechat(String wechat) { this.wechat = wechat; }
        public TalentStatus getStatus() { return status; }
        public void setStatus(TalentStatus status) { this.status = status; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
