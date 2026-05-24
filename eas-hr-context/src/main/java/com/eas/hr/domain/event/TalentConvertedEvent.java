package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 储备人才转化为员工领域事件
 */
public class TalentConvertedEvent extends DomainEvent {

    private final String employeeId;
    private final String talentName;

    public TalentConvertedEvent(String aggregateId, String employeeId, String talentName) {
        super(aggregateId, "Talent");
        this.employeeId = employeeId;
        this.talentName = talentName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getTalentName() {
        return talentName;
    }
}
