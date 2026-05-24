package com.eas.hr.message;

import com.eas.hr.domain.employee.Gender;

/**
 * 创建员工请求DTO
 */
public class CreateEmployeeRequest {

    private String name;
    private Gender gender;
    private String idCard;
    private String phone;
    private String email;
    private String departmentId;
    private String positionId;

    public CreateEmployeeRequest() {
    }

    public CreateEmployeeRequest(String name, Gender gender, String idCard, String phone, String email,
                                 String departmentId, String positionId) {
        this.name = name;
        this.gender = gender;
        this.idCard = idCard;
        this.phone = phone;
        this.email = email;
        this.departmentId = departmentId;
        this.positionId = positionId;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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
}
