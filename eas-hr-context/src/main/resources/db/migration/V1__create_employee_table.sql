-- 员工表
CREATE TABLE hr_employee (
    id VARCHAR(32) PRIMARY KEY COMMENT '员工ID',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) NOT NULL COMMENT '性别: MALE-男, FEMALE-女',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待入职, PROBATION-试用期, REGULAR-正式, TRANSFERRING-调岗中, RESIGNING-离职中, RESIGNED-已离职',
    hire_date DATE NOT NULL COMMENT '入职日期',
    department_id VARCHAR(32) NOT NULL COMMENT '部门ID',
    position_id VARCHAR(32) NOT NULL COMMENT '岗位ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_department (department_id),
    INDEX idx_position (position_id),
    INDEX idx_status (status),
    INDEX idx_hire_date (hire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 员工技能表
CREATE TABLE hr_employee_skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(32) NOT NULL COMMENT '员工ID',
    skill_name VARCHAR(100) NOT NULL COMMENT '技能名称',
    skill_level VARCHAR(20) NOT NULL COMMENT '技能等级: JUNIOR-初级, INTERMEDIATE-中级, SENIOR-高级, EXPERT-专家',
    certified_date DATE COMMENT '认证日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_employee_skill (employee_id, skill_name),
    INDEX idx_skill_name (skill_name),
    INDEX idx_skill_level (skill_level),
    FOREIGN KEY (employee_id) REFERENCES hr_employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工技能表';

-- 员工状态变更历史表
CREATE TABLE hr_employee_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(32) NOT NULL COMMENT '员工ID',
    from_status VARCHAR(20) COMMENT '原状态',
    to_status VARCHAR(20) NOT NULL COMMENT '新状态',
    change_time DATETIME NOT NULL COMMENT '变更时间',
    reason VARCHAR(500) COMMENT '变更原因',
    INDEX idx_employee (employee_id),
    INDEX idx_change_time (change_time),
    FOREIGN KEY (employee_id) REFERENCES hr_employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工状态变更历史表';
