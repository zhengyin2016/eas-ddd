-- 招聘需求表
CREATE TABLE hr_recruitment_requirement (
    id VARCHAR(32) PRIMARY KEY COMMENT '招聘需求ID',
    title VARCHAR(200) NOT NULL COMMENT '职位标题',
    department_id VARCHAR(32) NOT NULL COMMENT '部门ID',
    position_id VARCHAR(32) NOT NULL COMMENT '岗位ID',
    count INT NOT NULL COMMENT '招聘人数',
    description TEXT COMMENT '职位描述',
    requirements TEXT COMMENT '任职要求',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PENDING-待审批, APPROVED-已通过, REJECTED-已拒绝, CANCELLED-已取消, FULFILLED-已完成',
    approver VARCHAR(100) COMMENT '审批人',
    rejection_reason VARCHAR(500) COMMENT '拒绝原因',
    approved_at DATETIME COMMENT '审批时间',
    created_by VARCHAR(100) NOT NULL COMMENT '创建人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_department (department_id),
    INDEX idx_position (position_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘需求表';

-- 面试表
CREATE TABLE hr_interview (
    id VARCHAR(32) PRIMARY KEY COMMENT '面试ID',
    requirement_id VARCHAR(32) NOT NULL COMMENT '招聘需求ID',
    candidate_name VARCHAR(100) NOT NULL COMMENT '候选人姓名',
    candidate_phone VARCHAR(20) COMMENT '候选人电话',
    interview_time DATETIME NOT NULL COMMENT '面试时间',
    interviewer VARCHAR(100) NOT NULL COMMENT '面试官',
    result VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '面试结果: PENDING-待面试, PASSED-通过, FAILED-未通过',
    feedback TEXT COMMENT '面试反馈',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_requirement (requirement_id),
    INDEX idx_interview_time (interview_time),
    INDEX idx_result (result),
    FOREIGN KEY (requirement_id) REFERENCES hr_recruitment_requirement(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试表';
