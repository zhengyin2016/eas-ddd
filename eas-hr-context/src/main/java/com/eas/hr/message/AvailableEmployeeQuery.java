package com.eas.hr.message;

import java.time.LocalDate;
import java.util.Set;

/**
 * 查询可用员工请求DTO
 */
public class AvailableEmployeeQuery {

    private String departmentId;
    private String positionId;
    private Set<String> skillNames;
    private LocalDate availableDate;

    public AvailableEmployeeQuery() {
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public Set<String> getSkillNames() {
        return skillNames;
    }

    public void setSkillNames(Set<String> skillNames) {
        this.skillNames = skillNames;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }
}
