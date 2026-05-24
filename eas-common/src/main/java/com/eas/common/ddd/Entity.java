package com.eas.common.ddd;

import java.util.Objects;

/**
 * 实体基类
 * <p>
 * 实体具有唯一标识，通过标识判断相等性。
 * 即使实体的属性值相同，只要标识不同，就是不同的实体。
 * </p>
 *
 * @param <ID> 实体ID类型
 */
public abstract class Entity<ID extends Identity> {

    /**
     * 实体的唯一标识
     */
    protected final ID id;

    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
    }

    /**
     * 获取实体ID
     *
     * @return 实体ID
     */
    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Entity<?> other = (Entity<?>) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "]";
    }
}
