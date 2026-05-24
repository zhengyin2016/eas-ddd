-- PM上下文 - Project表
CREATE TABLE project (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_id VARCHAR(36),
    contract_id VARCHAR(36),
    pm_id VARCHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'PREPARING',
    budget DECIMAL(15, 2),
    start_date DATE,
    end_date DATE,
    version INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_contract_id (contract_id),
    INDEX idx_pm_id (pm_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
