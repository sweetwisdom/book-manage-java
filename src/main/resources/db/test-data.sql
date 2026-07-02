-- =============================================
-- 测试数据批量插入脚本
-- 注意：执行前请确保 Flyway 迁移已全部执行（V1 ~ V5）
-- 用法：直接在 MySQL 客户端执行此文件
--   mysql -u root -p test < test-data.sql
-- =============================================

-- =============================================
-- 1. 用户表测试数据
-- =============================================
-- 默认演示密码：123456
INSERT INTO `user` (`id`, `name`, `age`, `role`, `password_hash`, `remark`) VALUES
(1,  '管理员',   30, 'ADMIN',   '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '系统管理员'),
(2,  '张三',     25, 'VIP',     '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', 'VIP读者'),
(3,  '李四',     22, 'DEFAULT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '普通读者，借阅记录良好'),
(4,  '王五',     28, 'DEFAULT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '普通读者，有逾期记录'),
(5,  '赵六',     20, 'DEFAULT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '新注册读者，无借阅记录'),
(6,  '孙七',     35, 'TEACHER', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '教师读者'),
(7,  '周八',     19, 'STUDENT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '学生读者'),
(8,  '吴九',     40, 'DEFAULT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '资深读者'),
(9,  '郑十',     27, 'DEFAULT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '普通读者'),
(10, '陈一',     23, 'DEFAULT', '$2a$10$98.PnFOdPz4NEpH7PIeeSuqnOKz0qI6pK2itU8eGx4.ORJffus17S', '普通读者')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `password_hash` = VALUES(`password_hash`);

-- =============================================
-- 2. 图书分类表测试数据
-- =============================================
-- 一级分类
INSERT INTO `book_category` (`id`, `name`, `parent_id`, `sort_order`, `status`) VALUES
(1, '计算机技术', 0, 1, 1),
(2, '文学小说',   0, 2, 1),
(3, '科普读物',   0, 3, 1),
(4, '历史人文',   0, 4, 1),
(5, '经济管理',   0, 5, 0)   -- 已禁用
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 二级分类
INSERT INTO `book_category` (`id`, `name`, `parent_id`, `sort_order`, `status`) VALUES
(6,  '编程语言', 1, 1, 1),
(7,  '数据库',   1, 2, 1),
(8,  '人工智能', 1, 3, 1),
(9,  '中国文学', 2, 1, 1),
(10, '外国文学', 2, 2, 1),
(11, '物理科普', 3, 1, 1),
(12, '中国古代史', 4, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- =============================================
-- 3. 图书表测试数据
-- =============================================
INSERT INTO `book` (`id`, `isbn`, `title`, `author`, `publisher`, `publish_date`, `category_id`, `description`, `status`) VALUES
(1,  '978-7-111-00001', 'Java编程思想',       'Bruce Eckel',       '机械工业出版社', '2019-01-15', 6,  'Java语言经典教材，全面讲解面向对象编程思想', 1),
(2,  '978-7-111-00002', '深入理解MySQL',       '姜承尧',            '人民邮电出版社', '2020-05-20', 7,  'MySQL数据库原理与实践，涵盖索引优化和SQL调优', 1),
(3,  '978-7-111-00003', '深度学习入门',        '斋藤康毅',          '人民邮电出版社', '2021-03-10', 8,  '基于Python的深度学习理论与实践', 1),
(4,  '978-7-111-00004', '红楼梦',              '曹雪芹',            '人民文学出版社', '2018-08-01', 9,  '中国古典四大名著之首', 1),
(5,  '978-7-111-00005', '百年孤独',            '加西亚·马尔克斯',   '南海出版公司',   '2020-01-01', 10, '魔幻现实主义文学代表作', 1),
(6,  '978-7-111-00006', '时间简史',            '史蒂芬·霍金',       '湖南科技出版社', '2017-06-15', 11, '霍金代表作，探索宇宙与时间的奥秘', 1),
(7,  '978-7-111-00007', '万历十五年',          '黄仁宇',            '三联书店',       '2019-11-20', 12, '以万历十五年为切入点解读明朝兴衰', 1),
(8,  '978-7-111-00008', '算法导论',            'Thomas H.Cormen',  '机械工业出版社', '2020-09-01', 6,  '算法领域的经典教材', 0),  -- 已下架
(9,  '978-7-111-00009', 'Spring实战',          'Craig Walls',      '人民邮电出版社', '2021-06-01', 6,  'Spring框架核心技术与最佳实践', 1),
(10, '978-7-111-00010', '活着',               '余华',              '作家出版社',     '2019-05-10', 9,  '余华代表作，讲述生命中的苦难与坚韧', 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- =============================================
-- 4. 借阅规则表测试数据
-- =============================================
INSERT INTO `borrow_rule` (`id`, `role`, `max_borrow_count`, `max_borrow_days`, `max_renew_count`, `overdue_fee_per_day`, `status`) VALUES
(1, 'DEFAULT', 5,  30, 1, 0.50, 1),   -- 普通读者：最多5本，30天，可续借1次，逾期0.5元/天
(2, 'VIP',     10, 60, 3, 0.00, 1),   -- VIP读者：最多10本，60天，可续借3次，无逾期费
(3, 'TEACHER', 8,  45, 2, 0.20, 1),   -- 教师读者：最多8本，45天，可续借2次，逾期0.2元/天
(4, 'STUDENT', 3,  15, 1, 0.50, 1),   -- 学生读者：最多3本，15天，可续借1次
(5, 'DISABLED', 5, 30, 1, 1.00, 0)    -- 已禁用的规则
ON DUPLICATE KEY UPDATE `role` = VALUES(`role`);

-- =============================================
-- 5. 图书库存表测试数据
-- =============================================
INSERT INTO `book_stock` (`id`, `book_id`, `total_count`, `available_count`, `borrowed_count`, `lost_count`, `damaged_count`) VALUES
(1,  1,  10, 8,  2, 0, 0),   -- Java编程思想：2本借出
(2,  2,  5,  4,  1, 0, 0),   -- 深入理解MySQL：1本借出
(3,  3,  6,  6,  0, 0, 0),   -- 深度学习入门：全部在库
(4,  4,  8,  7,  1, 0, 0),   -- 红楼梦：1本借出
(5,  5,  4,  3,  0, 1, 0),   -- 百年孤独：有1本丢失
(6,  6,  3,  3,  0, 0, 0),   -- 时间简史：全部在库
(7,  7,  5,  5,  0, 0, 0),   -- 万历十五年：全部在库
(8,  8,  3,  0,  0, 0, 0),   -- 算法导论：已下架
(9,  9,  7,  6,  1, 0, 0),   -- Spring实战：1本借出（已逾期）
(10, 10, 10, 10, 0, 0, 0)    -- 活着：全部在库
ON DUPLICATE KEY UPDATE `available_count` = VALUES(`available_count`);

-- =============================================
-- 6. 借阅记录表测试数据
-- =============================================
INSERT INTO `borrow_record` (`id`, `user_id`, `book_id`, `borrow_time`, `due_time`, `return_time`, `status`, `renew_count`, `remark`) VALUES
-- 李四：正常借阅中 (borrowed)
(1, 3, 1, '2026-06-20 10:00:00', '2026-07-20 10:00:00', NULL, 'BORROWED', 0, NULL),
-- 李四：借阅中（近到期）
(2, 3, 2, '2026-06-05 09:00:00', '2026-07-05 09:00:00', NULL, 'BORROWED', 0, '即将到期'),
-- 李四：已归还
(3, 3, 4, '2026-05-01 14:00:00', '2026-05-31 14:00:00', '2026-05-28 16:30:00', 'RETURNED', 0, '提前归还'),
-- 王五：逾期未还 (borrowed but overdue)
(4, 4, 9, '2026-04-15 11:00:00', '2026-05-15 11:00:00', NULL, 'BORROWED', 0, '已逾期多日'),
-- 王五：已逾期但归还了
(5, 4, 5, '2026-03-01 08:00:00', '2026-03-31 08:00:00', '2026-04-10 10:00:00', 'RETURNED', 0, '逾期10天归还'),
-- 张三(VIP)：正常借阅中
(6, 2, 1, '2026-06-25 15:00:00', '2026-08-24 15:00:00', NULL, 'BORROWED', 0, 'VIP 60天借期'),
-- 张三(VIP)：借阅中（续借过）
(7, 2, 9, '2026-04-01 09:00:00', '2026-07-01 09:00:00', NULL, 'BORROWED', 2, '已续借2次，VIP专属'),
-- 周八(学生)：借阅中
(8, 7, 5, '2026-06-28 10:00:00', '2026-07-13 10:00:00', NULL, 'BORROWED', 0, '学生15天借期'),
-- 吴九：标记丢失
(9, 8, 5, '2026-02-10 13:00:00', '2026-03-12 13:00:00', NULL, 'LOST', 0, '读者声明丢失，正在处理赔偿'),
-- 郑十：已归还（续借过）
(10, 9, 3, '2026-05-10 10:00:00', '2026-07-09 10:00:00', '2026-06-30 16:00:00', 'RETURNED', 1, '续借1次后归还')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

-- =============================================
-- 7. 图书操作日志表测试数据
-- =============================================
INSERT INTO `book_operation_log` (`id`, `book_id`, `operator_id`, `operation_type`, `before_data`, `after_data`, `remark`, `create_time`) VALUES
(1, 1, 1, 'CREATE_BOOK',   NULL, '{"title":"Java编程思想","isbn":"978-7-111-00001"}', '管理员录入新书', '2026-01-10 09:00:00'),
(2, 1, 1, 'ADJUST_STOCK',  '{"total":5,"available":5}', '{"total":10,"available":10}', '采购入库5本', '2026-03-15 14:00:00'),
(3, 1, 3, 'BORROW_BOOK',   NULL, '借阅记录ID: 1', '李四借阅Java编程思想', '2026-06-20 10:00:00'),
(4, 9, 4, 'BORROW_BOOK',   NULL, '借阅记录ID: 4', '王五借阅Spring实战', '2026-04-15 11:00:00'),
(5, 3, 9, 'BORROW_BOOK',   NULL, '借阅记录ID: 10', '郑十借阅深度学习入门', '2026-05-10 10:00:00'),
(6, 3, 9, 'RETURN_BOOK',   '借阅记录ID: 10', '已归还', '郑十归还深度学习入门', '2026-06-30 16:00:00'),
(7, 5, 8, 'MARK_LOST',     '借阅记录ID: 9', '标记丢失', '吴九丢失百年孤独', '2026-05-10 09:00:00')
ON DUPLICATE KEY UPDATE `operation_type` = VALUES(`operation_type`);

-- =============================================
-- 验证数据：查看各表记录数
-- =============================================
SELECT 'user'               AS table_name, COUNT(*) AS row_count FROM `user`
UNION ALL
SELECT 'book_category',     COUNT(*) FROM `book_category`
UNION ALL
SELECT 'book',              COUNT(*) FROM `book`
UNION ALL
SELECT 'borrow_rule',       COUNT(*) FROM `borrow_rule`
UNION ALL
SELECT 'book_stock',        COUNT(*) FROM `book_stock`
UNION ALL
SELECT 'borrow_record',     COUNT(*) FROM `borrow_record`
UNION ALL
SELECT 'book_operation_log', COUNT(*) FROM `book_operation_log`;

-- =============================================
-- 查看各借阅状态的记录分布
-- =============================================
SELECT `status`, COUNT(*) AS cnt FROM `borrow_record` GROUP BY `status`;

-- =============================================
-- 查看有逾期的借阅记录（已借出但超过应还日期）
-- =============================================
SELECT
    br.id,
    u.name AS user_name,
    b.title AS book_title,
    br.borrow_time,
    br.due_time,
    DATEDIFF(NOW(), br.due_time) AS overdue_days
FROM `borrow_record` br
JOIN `user` u ON br.user_id = u.id
JOIN `book` b ON br.book_id = b.id
WHERE br.status = 'BORROWED'
  AND br.due_time < NOW();
