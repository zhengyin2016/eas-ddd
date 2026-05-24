package com.eas.hr.domain.event;

import com.eas.common.ddd.DomainEvent;

/**
 * 招聘需求审批通过领域事件
 */
public class RecruitmentApprovedEvent extends DomainEvent {

    private final String title;
    private final String approver;

    public RecruitmentApprovedEvent(String aggregateId, String title, String approver) {
        super(aggregateId, "RecruitmentRequirement");
        this.title = title;
        this.approver = approver;
    }

    public String getTitle() {
        return title;
    }

    public String getApprover() {
        return approver;
    }
}
