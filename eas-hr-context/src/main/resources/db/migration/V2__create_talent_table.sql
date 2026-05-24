-- 储备人才表
CREATE TABLE hr_talent (
    id VARCHAR(32) PRIMARY KEY COMMENT '储备人才ID',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    source VARCHAR(20) NOT NULL COMMENT '来源: WEBSITE-招聘网站, REFERRAL-内部推荐, HEADHUNTER-猎头, CAMPUS-校园招聘, SOCIAL-社交媒体, OTHER-其他',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    wechat VARCHAR(50) COMMENT '微信号',
    status VARCHAR(20) NOT NULL DEFAULT 'NEW' COMMENT '状态: NEW-新建, CONTACTING-联系中, INTERVIEWED-已面试, APPROVED-已通过, CONVERTED-已转化, REJECTED-已拒绝',
    notes TEXT COMMENT '备注',
    converted_employee_id VARCHAR(32) COMMENT '转化的员工ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_source (source),
    INDEX idx_phone (phone),
    INDEX idx_converted_employee (converted_employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='储备人才表';

-- 储备人才技能表
CREATE TABLE hr_talent_skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    talent_id VARCHAR(32) NOT NULL COMMENT '储备人才ID',
    skill_name VARCHAR(100) NOT NULL COMMENT '技能名称',
    skill_level VARCHAR(20) NOT NULL COMMENT '技能等级',
    certified_date DATE COMMENT '认证日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_talent_skill (talent_id, skill_name),
    INDEX idx_skill_name (skill_name),
    FOREIGN KEY (talent_id) REFERENCES hr_talent(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='储备人才技能表';
