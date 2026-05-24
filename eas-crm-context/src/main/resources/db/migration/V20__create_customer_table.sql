-- CRM上下文 - 客户表
CREATE TABLE customer (
    id VARCHAR(36) PRIMARY KEY COMMENT '客户ID',
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    industry VARCHAR(50) COMMENT '所属行业',
    level VARCHAR(10) NOT NULL DEFAULT 'D' COMMENT '客户等级: A/B/C/D',
    source VARCHAR(20) NOT NULL COMMENT '客户来源: REFERRAL/EXHIBITION/WEBSITE/COLD_CALL/OTHER',
    contact_name VARCHAR(50) NOT NULL COMMENT '主要联系人姓名',
    contact_phone VARCHAR(20) COMMENT '主要联系人电话',
    address VARCHAR(200) COMMENT '客户地址',
    creator_id VARCHAR(36) NOT NULL COMMENT '创建人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_customer_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 联系人表
CREATE TABLE contact (
    id VARCHAR(36) PRIMARY KEY COMMENT '联系人ID',
    customer_id VARCHAR(36) NOT NULL COMMENT '客户ID',
    name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    phone VARCHAR(20) COMMENT '联系人电话',
    email VARCHAR(100) COMMENT '联系人邮箱',
    position VARCHAR(50) COMMENT '联系人职位',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否主要联系人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';
