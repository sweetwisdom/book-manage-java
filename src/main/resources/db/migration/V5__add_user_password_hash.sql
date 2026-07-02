-- =============================================
-- V5: 用户表新增密码哈希字段
-- =============================================

ALTER TABLE `user`
    ADD COLUMN `password_hash` VARCHAR(255) DEFAULT NULL COMMENT '登录密码哈希'
    AFTER `role`;
