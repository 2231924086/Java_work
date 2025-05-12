-- 创建数据库
CREATE DATABASE IF NOT EXISTS task_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE task_management;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_user_category (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 任务表
CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    priority TINYINT NOT NULL DEFAULT 2 COMMENT '1:低, 2:中, 3:高',
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入测试数据
INSERT INTO users (username, password, email, phone) VALUES
('admin', '$2a$10$X/hX4Jz9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9', 'admin@example.com', '13800138000');

INSERT INTO categories (user_id, name, description) VALUES
(1, '工作', '工作相关任务'),
(1, '学习', '学习相关任务'),
(1, '生活', '生活相关任务');

INSERT INTO tasks (user_id, category_id, title, content, status, priority, due_date) VALUES
(1, 1, '完成项目文档', '编写项目需求文档和设计文档', 'PENDING', 3, DATE_ADD(NOW(), INTERVAL 7 DAY)),
(1, 2, '学习Vue.js', '学习Vue.js基础知识和组件开发', 'IN_PROGRESS', 2, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(1, 3, '购物清单', '购买生活必需品', 'PENDING', 1, DATE_ADD(NOW(), INTERVAL 3 DAY)); 