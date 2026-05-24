-- CRM上下文 - 合同表
CREATE TABLE contract (
    id VARCHAR(36) PRIMARY KEY COMMENT '合同ID',
    customer_id VARCHAR(36) NOT NULL COMMENT '客户ID',
    opportunity_id VARCHAR(36) COMMENT '商机ID',
    title VARCHAR(200) NOT NULL COMMENT '合同标题',
    amount DECIMAL(15,2) NOT NULL COMMENT '合同金额',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '合同状态: DRAFT/UNDER_REVIEW/APPROVED/ACTIVE/COMPLETED/TERMINATED',
    sign_date DATE COMMENT '签约日期',
    start_date DATE COMMENT '生效日期',
    end_date DATE COMMENT '终止日期',
    paid_amount DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '已回款金额',
    approver_id VARCHAR(36) COMMENT '审核人ID',
    reject_reason VARCHAR(200) COMMENT '拒绝原因',
    terminate_reason VARCHAR(200) COMMENT '终止原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE RESTRICT,
    FOREIGN KEY (opportunity_id) REFERENCES opportunity(id) ON DELETE SET NULL,
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- 回款计划表
CREATE TABLE payment_plan (
    id VARCHAR(36) PRIMARY KEY COMMENT '回款计划ID',
    contract_id VARCHAR(36) NOT NULL COMMENT '合同ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '计划金额',
    due_date DATE NOT NULL COMMENT '计划回款日期',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/PAID/OVERDUE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    paid_at DATETIME COMMENT '实际回款时间',
    FOREIGN KEY (contract_id) REFERENCES contract(id) ON DELETE CASCADE,
    INDEX idx_contract_id (contract_id),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回款计划表';
