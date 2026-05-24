package com.eas.common.ddd;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * <p>
 * 领域事件表示领域内发生的重要业务事件。
 * 事件是不可变的，发生后不应被修改。
 * </p>
 */
public abstract class DomainEvent {

    /**
     * 事件唯一ID
     */
    private final String eventId;

    /**
     * 事件发生时间
     */
    private final LocalDateTime occurredOn;

    /**
     * 事件聚合根ID
     */
    private final String aggregateId;

    /**
     * 事件聚合根类型
     */
    private final String aggregateType;

    protected DomainEvent(String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    /**
     * 获取事件类型名称
     * 默认使用类名，子类可以覆盖
     *
     * @return 事件类型名称
     */
    public String getEventType() {
        return getClass().getSimpleName();
    }
}
