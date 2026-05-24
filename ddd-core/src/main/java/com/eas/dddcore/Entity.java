package com.eas.dddcore;

import java.util.Objects;

/**
 * 实体基类
 * 实体拥有唯一标识，通过标识判等。
 *
 * @param <ID> 实体标识类型
 */
public abstract class Entity<ID> {

    protected ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
