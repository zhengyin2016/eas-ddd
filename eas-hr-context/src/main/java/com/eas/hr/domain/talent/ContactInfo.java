package com.eas.hr.domain.talent;

import com.eas.common.ddd.ValueObject;

import java.util.Objects;

/**
 * 联系信息值对象
 *
 * @param phone  手机号
 * @param email  邮箱
 * @param wechat 微信号
 */
public record ContactInfo(String phone, String email, String wechat) implements ValueObject {

    public ContactInfo {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
    }

    public static ContactInfo of(String phone, String email) {
        return new ContactInfo(phone, email, null);
    }

    public static ContactInfo of(String phone, String email, String wechat) {
        return new ContactInfo(phone, email, wechat);
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasWechat() {
        return wechat != null && !wechat.isBlank();
    }
}
