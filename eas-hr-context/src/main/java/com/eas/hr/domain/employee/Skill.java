package com.eas.hr.domain.employee;

import com.eas.common.ddd.ValueObject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 技能值对象
 *
 * @param name           技能名称
 * @param level          技能等级
 * @param certifiedDate 认证日期（可选）
 */
public record Skill(String name, SkillLevel level, LocalDate certifiedDate) implements ValueObject {

    public Skill {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be null or blank");
        }
        if (level == null) {
            throw new IllegalArgumentException("Skill level cannot be null");
        }
    }

    /**
     * 创建无认证证书的技能
     *
     * @param name  技能名称
     * @param level 技能等级
     * @return 技能值对象
     */
    public static Skill of(String name, SkillLevel level) {
        return new Skill(name, level, null);
    }

    /**
     * 创建有认证证书的技能
     *
     * @param name           技能名称
     * @param level          技能等级
     * @param certifiedDate 认证日期
     * @return 技能值对象
     */
    public static Skill certified(String name, SkillLevel level, LocalDate certifiedDate) {
        return new Skill(name, level, Objects.requireNonNull(certifiedDate, "Certified date cannot be null"));
    }

    /**
     * 检查是否有认证证书
     *
     * @return true如果有认证证书
     */
    public boolean hasCertificate() {
        return certifiedDate != null;
    }
}
