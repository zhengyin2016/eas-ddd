package com.eas.common.ddd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 * <p>
 * 聚合根是聚合的唯一入口点，负责维护聚合内部的数据一致性。
 * 所有外部访问聚合内的对象必须通过聚合根进行。
 * </p>
 *
 * @param <ID> 聚合根ID类型
 */
public abstract class AggregateRoot<ID extends Identity> extends Entity<ID> {

    /**
     * 领域事件列表
     */
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(ID id) {
        super(id);
    }

    /**
     * 获取所有未发布的领域事件
     *
     * @return 不可修改的领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 添加领域事件
     *
     * @param event 领域事件
     */
    protected void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 清空已发布的领域事件
     * <p>
     * 通常在事件发布后调用此方法，避免重复发布。
     * </p>
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    /**
     * 检查是否有未发布的领域事件
     *
     * @return true如果有未发布的事件
     */
    public boolean hasUnpublishedEvents() {
        return !domainEvents.isEmpty();
    }
}
