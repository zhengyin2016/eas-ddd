package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 招聘需求提交领域事件
 */
public class RecruitmentSubmittedEvent extends DomainEvent {

    private final String title;
    private final String departmentId;

    public RecruitmentSubmittedEvent(String aggregateId, String title, String departmentId) {
        super(aggregateId, "RecruitmentRequirement");
        this.title = title;
        this.departmentId = departmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDepartmentId() {
        return departmentId;
    }
}
