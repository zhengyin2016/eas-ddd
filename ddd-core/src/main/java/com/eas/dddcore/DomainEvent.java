package com.eas.dddcore;

import java.time.Instant;

/**
 * 领域事件基类
 * 所有领域事件都继承此类，携带事件发生时间。
 */
public abstract class DomainEvent {

    private final Instant occurredOn;

    protected DomainEvent() {
        this.occurredOn = Instant.now();
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
