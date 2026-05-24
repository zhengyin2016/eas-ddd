package com.eas.hr.message;

import com.eas.hr.domain.talent.TalentSource;
import com.eas.hr.domain.talent.TalentStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 储备人才响应DTO
 */
public class TalentResponse {

    private String id;
    private String name;
    private TalentSource source;
    private String phone;
    private String email;
    private String wechat;
    private TalentStatus status;
    private List<SkillResponse> skills;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TalentResponse() {
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TalentSource getSource() {
        return source;
    }

    public void setSource(TalentSource source) {
        this.source = source;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public TalentStatus getStatus() {
        return status;
    }

    public void setStatus(TalentStatus status) {
        this.status = status;
    }

    public List<SkillResponse> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillResponse> skills) {
        this.skills = skills;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
