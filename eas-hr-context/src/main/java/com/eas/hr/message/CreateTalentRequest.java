package com.eas.hr.message;

import com.eas.hr.domain.talent.TalentSource;

import java.util.List;

/**
 * 创建储备人才请求DTO
 */
public class CreateTalentRequest {

    private String name;
    private TalentSource source;
    private String phone;
    private String email;
    private String wechat;
    private List<SkillRequest> skills;
    private String notes;

    public CreateTalentRequest() {
    }

    // Getters and Setters

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

    public List<SkillRequest> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillRequest> skills) {
        this.skills = skills;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * 技能请求DTO
     */
    public static class SkillRequest {
        private String name;
        private String level;
        private String certifiedDate;

        public SkillRequest() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getCertifiedDate() {
            return certifiedDate;
        }

        public void setCertifiedDate(String certifiedDate) {
            this.certifiedDate = certifiedDate;
        }
    }
}
