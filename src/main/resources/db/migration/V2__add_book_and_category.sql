-- =============================================
-- V2: 图书分类表和图书表
-- =============================================

CREATE TABLE IF NOT EXISTS `book_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_category_name_parent` (`name`, `parent_id`),
    KEY `idx_book_category_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `isbn` VARCHAR(32) DEFAULT NULL COMMENT 'ISBN编号',
    `title` VARCHAR(128) NOT NULL COMMENT '书名',
    `author` VARCHAR(128) DEFAULT NULL COMMENT '作者',
    `publisher` VARCHAR(128) DEFAULT NULL COMMENT '出版社',
    `publish_date` DATE DEFAULT NULL COMMENT '出版日期',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '图书简介',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架，0下架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_isbn` (`isbn`),
    KEY `idx_book_title` (`title`),
    KEY `idx_book_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';
