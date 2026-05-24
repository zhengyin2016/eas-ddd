-- CRM上下文 - 回款记录表
CREATE TABLE payment (
    id VARCHAR(36) PRIMARY KEY COMMENT '回款ID',
    contract_id VARCHAR(36) NOT NULL COMMENT '合同ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '回款金额',
    payment_date DATE NOT NULL COMMENT '回款日期',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式: BANK_TRANSFER/CHECK/CASH/ELECTRONIC',
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED' COMMENT '状态: PLANNED/PAID/OVERDUE/CANCELLED',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    confirmed_at DATETIME COMMENT '确认时间',
    FOREIGN KEY (contract_id) REFERENCES contract(id) ON DELETE RESTRICT,
    INDEX idx_contract_id (contract_id),
    INDEX idx_status (status),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回款记录表';
