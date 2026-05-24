package com.eas.dddcore;

/**
 * 标识值对象
 * 使用 Java Record 实现不可变的唯一标识。
 *
 * @param value 标识的字符串值
 */
public record Identity(String value) implements ValueObject {

    public Identity {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("标识值不能为空");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
