package com.eas.hr.message;

/**
 * 更新员工请求DTO
 */
public class UpdateEmployeeRequest {

    private String name;
    private String phone;
    private String email;

    public UpdateEmployeeRequest() {
    }

    public UpdateEmployeeRequest(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
