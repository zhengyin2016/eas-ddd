-- 考勤记录表
CREATE TABLE hr_attendance (
    id VARCHAR(32) PRIMARY KEY COMMENT '考勤ID',
    employee_id VARCHAR(32) NOT NULL COMMENT '员工ID',
    date DATE NOT NULL COMMENT '考勤日期',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    status VARCHAR(20) NOT NULL DEFAULT 'ABSENT' COMMENT '考勤状态: NORMAL-正常, LATE-迟到, EARLY_LEAVE-早退, ABSENT-缺勤',
    work_hours DECIMAL(4,2) DEFAULT 0.00 COMMENT '工作时长(小时)',
    notes VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_employee_date (employee_id, date),
    INDEX idx_employee (employee_id),
    INDEX idx_date (date),
    INDEX idx_status (status),
    FOREIGN KEY (employee_id) REFERENCES hr_employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';
