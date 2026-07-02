-- =============================================
-- V4: 用户表新增角色字段
-- =============================================

ALTER TABLE `user`
    ADD COLUMN `role` VARCHAR(32) NOT NULL DEFAULT 'DEFAULT' COMMENT '用户角色：DEFAULT普通读者,VIP会员,TEACHER教师,STUDENT学生,ADMIN管理员'
    AFTER `age`;

-- 为角色字段添加索引，便于按角色查询用户
ALTER TABLE `user`
    ADD INDEX `idx_user_role` (`role`);
