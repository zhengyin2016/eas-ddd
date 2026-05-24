package com.eas.dddcore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 * 聚合根是聚合的入口点，管理领域事件的生命周期。
 *
 * @param <ID> 聚合根标识类型
 */
public abstract class AggregateRoot<ID> extends Entity<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 注册领域事件
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * 获取所有未提交的领域事件（只读视图）
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 清除所有已提交的领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
