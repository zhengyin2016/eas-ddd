-- EAS 培训上下文数据库表结构
-- 培训上下文包含以下聚合：Training、Course、Ticket、TicketHistory、
-- Candidate、Learning、Filter、ValidDate、ValidDateAction、
-- CancellingAction、Attendance、Blacklist

-- ============================================================
-- 培训表
-- ============================================================
CREATE TABLE IF NOT EXISTS training (
    id              VARCHAR(64)  NOT NULL COMMENT '培训ID',
    course_id       VARCHAR(64)  NOT NULL COMMENT '课程ID',
    title           VARCHAR(256) NOT NULL COMMENT '培训标题',
    description     TEXT         NULL     COMMENT '培训描述',
    trainer_id      VARCHAR(64)  NULL     COMMENT '培训师ID',
    start_time      DATETIME     NOT NULL COMMENT '培训开始时间',
    end_time        DATETIME     NOT NULL COMMENT '培训结束时间',
    status          VARCHAR(32)  NOT NULL COMMENT '培训状态：DRAFT/PLANNED/IN_PROGRESS/COMPLETED/CANCELLED',
    nomination_deadline  DATETIME NULL     COMMENT '提名截止时间',
    absence_deadline     DATETIME NULL     COMMENT '缺席截止时间',
    created_by      VARCHAR(64)  NOT NULL COMMENT '创建人',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训';

-- ============================================================
-- 课程表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_course (
    id              VARCHAR(64)  NOT NULL COMMENT '课程ID',
    name            VARCHAR(256) NOT NULL COMMENT '课程名称',
    description     TEXT         NULL     COMMENT '课程描述',
    category        VARCHAR(64)  NULL     COMMENT '课程分类',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训课程';

-- ============================================================
-- 培训票表（核心表）
-- ============================================================
CREATE TABLE IF NOT EXISTS training_ticket (
    id              VARCHAR(64)  NOT NULL COMMENT '票ID',
    training_id     VARCHAR(64)  NOT NULL COMMENT '培训ID',
    coordinator_id  VARCHAR(64)  NULL     COMMENT '协调者ID（票分配给的部门协调者）',
    nominee_id      VARCHAR(64)  NULL     COMMENT '被提名人ID（被提名参加培训的员工）',
    status          VARCHAR(32)  NOT NULL COMMENT '票状态：AVAILABLE/WAIT_FOR_CONFIRM/CONFIRMED/CANCELLED/EXPIRED',
    valid_date_id   VARCHAR(64)  NULL     COMMENT '关联的有效日期ID',
    created_by      VARCHAR(64)  NOT NULL COMMENT '创建人',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_training_ticket_training_id (training_id),
    INDEX idx_training_ticket_coordinator_id (coordinator_id),
    INDEX idx_training_ticket_nominee_id (nominee_id),
    INDEX idx_training_ticket_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训票';

-- ============================================================
-- 培训票历史表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_ticket_history (
    id                  VARCHAR(64)  NOT NULL COMMENT '历史记录ID',
    ticket_id           VARCHAR(64)  NOT NULL COMMENT '票ID',
    operation_type      VARCHAR(32)  NOT NULL COMMENT '操作类型：NOMINATE/CONFIRM/DECLINE/ALLOCATE/CANCEL',
    state_transit_from  VARCHAR(32)  NOT NULL COMMENT '状态迁移-起始状态',
    state_transit_to    VARCHAR(32)  NOT NULL COMMENT '状态迁移-目标状态',
    ticket_owner_id     VARCHAR(64)  NULL     COMMENT '票拥有者员工ID',
    ticket_owner_name   VARCHAR(128) NULL     COMMENT '票拥有者姓名',
    operator_id         VARCHAR(64)  NOT NULL COMMENT '操作人员工ID',
    operator_name       VARCHAR(128) NOT NULL COMMENT '操作人姓名',
    operated_at         DATETIME     NOT NULL COMMENT '操作时间',
    PRIMARY KEY (id),
    INDEX idx_ticket_history_ticket_id (ticket_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训票历史';

-- ============================================================
-- 候选人表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_candidate (
    id              VARCHAR(64)  NOT NULL COMMENT '候选人ID',
    training_id     VARCHAR(64)  NOT NULL COMMENT '培训ID',
    employee_id     VARCHAR(64)  NOT NULL COMMENT '员工ID',
    employee_name   VARCHAR(128) NOT NULL COMMENT '员工姓名',
    department_id   VARCHAR(64)  NULL     COMMENT '部门ID',
    source          VARCHAR(32)  NOT NULL COMMENT '来源：FILTER/MANUAL',
    nominated       BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否已被提名',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_candidate_training_employee (training_id, employee_id),
    INDEX idx_candidate_training_id (training_id),
    INDEX idx_candidate_nominated (training_id, nominated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训候选人';

-- ============================================================
-- 学习记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_learning (
    id              VARCHAR(64)  NOT NULL COMMENT '学习记录ID',
    course_id       VARCHAR(64)  NOT NULL COMMENT '课程ID',
    employee_id     VARCHAR(64)  NOT NULL COMMENT '员工ID',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_learning_course_employee (course_id, employee_id),
    INDEX idx_learning_course_id (course_id),
    INDEX idx_learning_employee_id (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训学习记录';

-- ============================================================
-- 筛选器表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_filter (
    id              VARCHAR(64)  NOT NULL COMMENT '筛选器ID',
    training_id     VARCHAR(64)  NOT NULL COMMENT '培训ID',
    name            VARCHAR(128) NOT NULL COMMENT '筛选器名称',
    filter_type     VARCHAR(32)  NOT NULL COMMENT '筛选类型：DEPARTMENT/POSITION/SKILL/CUSTOM',
    filter_rule     TEXT         NOT NULL COMMENT '筛选规则（JSON格式）',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_filter_training_id (training_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训筛选器';

-- ============================================================
-- 有效日期表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_valid_date (
    id              VARCHAR(64)  NOT NULL COMMENT '有效日期ID',
    training_id     VARCHAR(64)  NOT NULL COMMENT '培训ID',
    deadline_type   VARCHAR(32)  NOT NULL COMMENT '截止类型：NOMINATION/ABSENCE/START_BEFORE/END_BEFORE',
    deadline_time   DATETIME     NOT NULL COMMENT '截止时间',
    action_strategy VARCHAR(32)  NULL     COMMENT '活动策略：SHARE/ALLOCATE/VOID',
    auto_process    BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否自动处理',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_valid_date_training_id (training_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训有效日期';

-- ============================================================
-- 有效日期动作表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_valid_date_action (
    id              VARCHAR(64)  NOT NULL COMMENT '动作ID',
    valid_date_id   VARCHAR(64)  NOT NULL COMMENT '有效日期ID',
    action_type     VARCHAR(32)  NOT NULL COMMENT '动作类型：SHARE_TO_COORDINATOR/ALLOCATE_TO_EMPLOYEE/VOID_TICKET',
    action_config   TEXT         NULL     COMMENT '动作配置（JSON格式）',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_valid_date_action_valid_date_id (valid_date_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训有效日期动作';

-- ============================================================
-- 取消动作表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_cancelling_action (
    id              VARCHAR(64)  NOT NULL COMMENT '取消动作ID',
    ticket_id       VARCHAR(64)  NOT NULL COMMENT '票ID',
    training_id     VARCHAR(64)  NOT NULL COMMENT '培训ID',
    canceller_id    VARCHAR(64)  NOT NULL COMMENT '取消人ID',
    canceller_role  VARCHAR(32)  NOT NULL COMMENT '取消人角色：TRAINING_SPECIALIST/COORDINATOR/EMPLOYEE/SYSTEM',
    cancel_reason   TEXT         NULL     COMMENT '取消原因',
    deadline_type   VARCHAR(32)  NOT NULL COMMENT '取消时的截止类型',
    action_taken    VARCHAR(32)  NOT NULL COMMENT '采取的动作',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_cancelling_action_ticket_id (ticket_id),
    INDEX idx_cancelling_action_training_id (training_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训取消动作';

-- ============================================================
-- 出勤表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_attendance (
    id              VARCHAR(64)  NOT NULL COMMENT '出勤记录ID',
    training_id     VARCHAR(64)  NOT NULL COMMENT '培训ID',
    employee_id     VARCHAR(64)  NOT NULL COMMENT '员工ID',
    employee_name   VARCHAR(128) NOT NULL COMMENT '员工姓名',
    check_in_time   DATETIME     NULL     COMMENT '签到时间',
    check_out_time  DATETIME     NULL     COMMENT '签退时间',
    attendance_type VARCHAR(32)  NOT NULL COMMENT '出勤类型：CHECK_IN/CHECK_OUT',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_attendance_training_id (training_id),
    INDEX idx_attendance_employee_id (employee_id),
    UNIQUE INDEX uk_attendance_training_employee_type (training_id, employee_id, attendance_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训出勤';

-- ============================================================
-- 黑名单表
-- ============================================================
CREATE TABLE IF NOT EXISTS training_blacklist (
    id              VARCHAR(64)  NOT NULL COMMENT '黑名单记录ID',
    employee_id     VARCHAR(64)  NOT NULL COMMENT '员工ID',
    employee_name   VARCHAR(128) NOT NULL COMMENT '员工姓名',
    training_id     VARCHAR(64)  NULL     COMMENT '触发黑名单的培训ID',
    reason          VARCHAR(256) NOT NULL COMMENT '加入黑名单原因',
    created_at      DATETIME     NOT NULL COMMENT '创建时间',
    removed         BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否已移出',
    removed_at      DATETIME     NULL     COMMENT '移出时间',
    PRIMARY KEY (id),
    INDEX idx_blacklist_employee_id (employee_id),
    INDEX idx_blacklist_removed (employee_id, removed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训黑名单';
