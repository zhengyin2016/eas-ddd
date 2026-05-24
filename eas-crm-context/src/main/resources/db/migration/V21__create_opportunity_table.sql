-- CRM上下文 - 商机表
CREATE TABLE opportunity (
    id VARCHAR(36) PRIMARY KEY COMMENT '商机ID',
    customer_id VARCHAR(36) NOT NULL COMMENT '客户ID',
    title VARCHAR(200) NOT NULL COMMENT '商机标题',
    estimated_amount DECIMAL(15,2) NOT NULL COMMENT '预计金额',
    stage VARCHAR(30) NOT NULL DEFAULT 'INITIAL_CONTACT' COMMENT '商机阶段: INITIAL_CONTACT/NEEDS_CONFIRMATION/PROPOSAL/NEGOTIATION/CONTRACT_SIGNING',
    probability INT NOT NULL DEFAULT 20 COMMENT '赢率百分比',
    expected_close_date DATE COMMENT '预计成交日期',
    owner_id VARCHAR(36) NOT NULL COMMENT '负责人ID',
    is_won BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否赢单',
    is_lost BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否输单',
    lost_reason VARCHAR(200) COMMENT '输单原因',
    actual_amount DECIMAL(15,2) COMMENT '实际成交金额',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE RESTRICT,
    INDEX idx_customer_id (customer_id),
    INDEX idx_stage (stage),
    INDEX idx_owner_id (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机表';
