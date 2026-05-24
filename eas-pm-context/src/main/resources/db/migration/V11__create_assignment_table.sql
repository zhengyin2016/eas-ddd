-- PM上下文 - Assignment表
CREATE TABLE assignment (
    id VARCHAR(36) PRIMARY KEY,
    project_id VARCHAR(36) NOT NULL,
    employee_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    allocation INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    released BOOLEAN NOT NULL DEFAULT FALSE,
    released_at DATE,
    version INT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    INDEX idx_project_id (project_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_released (released)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
