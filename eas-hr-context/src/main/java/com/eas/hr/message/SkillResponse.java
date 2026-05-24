package com.eas.hr.message;

import com.eas.hr.domain.employee.SkillLevel;

import java.time.LocalDate;

/**
 * 技能响应DTO
 */
public class SkillResponse {

    private String name;
    private SkillLevel level;
    private LocalDate certifiedDate;

    public SkillResponse() {
    }

    public SkillResponse(String name, SkillLevel level, LocalDate certifiedDate) {
        this.name = name;
        this.level = level;
        this.certifiedDate = certifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillLevel getLevel() {
        return level;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public LocalDate getCertifiedDate() {
        return certifiedDate;
    }

    public void setCertifiedDate(LocalDate certifiedDate) {
        this.certifiedDate = certifiedDate;
    }
}
