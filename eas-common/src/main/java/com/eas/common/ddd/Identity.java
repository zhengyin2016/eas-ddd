package com.eas.common.ddd;

import java.util.Objects;

/**
 * 标识值对象基类
 * <p>
 * 所有ID值对象应继承此类。
 * 标识是不可变的，通过值判断相等性。
 * </p>
 *
 * @param <T> 标识值的实际类型
 */
public abstract class Identity<T> implements ValueObject {

    /**
     * 标识值
     */
    private final T value;

    protected Identity(T value) {
        this.value = Objects.requireNonNull(value, "Identity value cannot be null");
    }

    /**
     * 获取标识值
     *
     * @return 标识值
     */
    public T value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Identity<?> other = (Identity<?>) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
