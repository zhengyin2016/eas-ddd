package com.eas.common.ddd;

/**
 * 值对象标记接口
 * <p>
 * 值对象通过属性值判断相等性，没有唯一标识。
 * 值对象应该是不可变的（immutable）。
 * </p>
 * <p>
 * 实现建议：使用Java Record实现值对象，Record类自动实现equals/hashCode/toString。
 * </p>
 */
public interface ValueObject {
    // 标记接口，无方法定义
}
