-- 培训计划表
CREATE TABLE hr_training_plan (
    id VARCHAR(32) PRIMARY KEY COMMENT '培训计划ID',
    name VARCHAR(200) NOT NULL COMMENT '培训名称',
    description TEXT COMMENT '培训描述',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    capacity INT NOT NULL COMMENT '容量限制',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    instructor VARCHAR(100) COMMENT '讲师',
    location VARCHAR(200) COMMENT '培训地点',
    created_by VARCHAR(100) NOT NULL COMMENT '创建人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训计划表';

-- 培训报名表
CREATE TABLE hr_training_enrollment (
    id VARCHAR(32) PRIMARY KEY COMMENT '报名ID',
    plan_id VARCHAR(32) NOT NULL COMMENT '培训计划ID',
    employee_id VARCHAR(32) NOT NULL COMMENT '员工ID',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '报名状态: PENDING-待确认, CONFIRMED-已确认, ATTENDED-已签到, COMPLETED-已完成, CANCELLED-已取消',
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    check_in_time DATETIME COMMENT '签到时间',
    completion_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成进度(%)',
    notes VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_plan_employee (plan_id, employee_id),
    INDEX idx_plan (plan_id),
    INDEX idx_employee (employee_id),
    INDEX idx_status (status),
    FOREIGN KEY (plan_id) REFERENCES hr_training_plan(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES hr_employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训报名表';
