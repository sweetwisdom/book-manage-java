-- =============================================
-- V3: 图书库存表 图书操作日志表 借阅规则表
-- =============================================

CREATE TABLE IF NOT EXISTS `book_stock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `total_count` INT NOT NULL DEFAULT 0 COMMENT '总库存数量',
  `available_count` INT NOT NULL DEFAULT 0 COMMENT '可借数量',
  `borrowed_count` INT NOT NULL DEFAULT 0 COMMENT '已借出数量',
  `lost_count` INT NOT NULL DEFAULT 0 COMMENT '丢失数量',
  `damaged_count` INT NOT NULL DEFAULT 0 COMMENT '损坏数量',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_stock_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书库存表';

CREATE TABLE IF NOT EXISTS `borrow_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `borrow_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借出时间',
  `due_time` DATETIME NOT NULL COMMENT '应还时间',
  `return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'BORROWED' COMMENT '借阅状态',
  `renew_count` INT NOT NULL DEFAULT 0 COMMENT '续借次数',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_borrow_record_user_id` (`user_id`),
  KEY `idx_borrow_record_book_id` (`book_id`),
  KEY `idx_borrow_record_status` (`status`),
  KEY `idx_borrow_record_due_time` (`due_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

CREATE TABLE IF NOT EXISTS `book_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT DEFAULT NULL COMMENT '图书ID',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型',
  `before_data` TEXT DEFAULT NULL COMMENT '操作前数据',
  `after_data` TEXT DEFAULT NULL COMMENT '操作后数据',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_operation_log_book_id` (`book_id`),
  KEY `idx_book_operation_log_operator_id` (`operator_id`),
  KEY `idx_book_operation_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书操作日志表';

CREATE TABLE IF NOT EXISTS `borrow_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role` VARCHAR(32) NOT NULL COMMENT '用户角色',
  `max_borrow_count` INT NOT NULL DEFAULT 5 COMMENT '最大可借数量',
  `max_borrow_days` INT NOT NULL DEFAULT 30 COMMENT '最大借阅天数',
  `max_renew_count` INT NOT NULL DEFAULT 1 COMMENT '最大续借次数',
  `overdue_fee_per_day` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '每日逾期费用',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_borrow_rule_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅规则表';