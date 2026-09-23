-- 媒体素材管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS media_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE media_db;

-- ============================================
-- 系统管理模块表
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) COMMENT '菜单路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    permission VARCHAR(100) COMMENT '权限标识',
    menu_type TINYINT DEFAULT 1 COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
    sort INT DEFAULT 0 COMMENT '排序',
    visible TINYINT DEFAULT 1 COMMENT '是否可见：0-隐藏，1-显示',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================================
-- 素材管理模块表
-- ============================================

-- 素材分类表
CREATE TABLE IF NOT EXISTS media_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) COMMENT '分类编码',
    icon VARCHAR(100) COMMENT '分类图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    remark VARCHAR(255) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材分类表';

-- 素材表
CREATE TABLE IF NOT EXISTS media (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '素材ID',
    title VARCHAR(200) NOT NULL COMMENT '素材标题',
    description TEXT COMMENT '素材描述',
    media_type VARCHAR(20) NOT NULL COMMENT '素材类型：image/video/audio/document',
    category_id BIGINT COMMENT '分类ID',
    file_path VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_name VARCHAR(100) COMMENT '文件名',
    original_name VARCHAR(200) COMMENT '原始文件名',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_ext VARCHAR(20) COMMENT '文件扩展名',
    cover_path VARCHAR(255) COMMENT '封面图路径',
    tags VARCHAR(500) COMMENT '标签（逗号分隔）',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    status TINYINT DEFAULT 0 COMMENT '状态：0-下架，1-上架',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    create_by BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_category (category_id),
    INDEX idx_type (media_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材表';

-- 标签表
CREATE TABLE IF NOT EXISTS media_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    tag_color VARCHAR(20) COMMENT '标签颜色',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- AI 打标签 bad case 记录表（用于产品迭代复盘：AI 建议 vs 用户最终值）
CREATE TABLE IF NOT EXISTS ai_bad_case (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    media_id BIGINT COMMENT '关联素材ID（可选）',
    ai_title VARCHAR(200) COMMENT 'AI 建议标题',
    ai_description TEXT COMMENT 'AI 建议描述',
    ai_tags VARCHAR(500) COMMENT 'AI 建议标签（逗号分隔）',
    ai_category VARCHAR(50) COMMENT 'AI 建议分类名',
    final_title VARCHAR(200) COMMENT '用户最终标题',
    final_description TEXT COMMENT '用户最终描述',
    final_tags VARCHAR(500) COMMENT '用户最终标签（逗号分隔）',
    final_category_id BIGINT COMMENT '用户最终分类ID',
    adopted TINYINT DEFAULT 1 COMMENT '是否采纳AI：1-采纳，0-修改',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI打标签bad case记录表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员用户（默认密码见 README，使用 BCrypt 加密）
INSERT INTO sys_user (username, password, nickname, email, gender, status, remark) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'admin@example.com', 1, 1, '系统管理员'),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '普通用户', 'user@example.com', 0, 1, '普通用户');

-- 插入默认角色
INSERT INTO sys_role (role_name, role_code, sort, status, remark) VALUES
('管理员', 'ADMIN', 1, 1, '系统管理员角色'),
('普通用户', 'USER', 2, 1, '普通用户角色');

-- 插入默认菜单
INSERT INTO sys_menu (parent_id, menu_name, path, icon, permission, menu_type, sort, visible, status) VALUES
(0, '首页', '/', 'home', '', 1, 1, 1, 1),
(0, '素材管理', '/media', 'folder', 'media:view', 0, 2, 1, 1),
(2, '素材列表', '/media/list', 'list', 'media:list', 1, 1, 1, 1),
(2, '素材上传', '/media/upload', 'upload', 'media:upload', 1, 2, 1, 1),
(2, '分类管理', '/media/category', 'folder', 'category:manage', 1, 3, 1, 1),
(0, '系统管理', '/system', 'settings', 'system:view', 0, 3, 1, 1),
(6, '用户管理', '/system/user', 'user', 'user:manage', 1, 1, 1, 1),
(6, '角色管理', '/system/role', 'role', 'role:manage', 1, 2, 1, 1),
(6, '菜单管理', '/system/menu', 'menu', 'menu:manage', 1, 3, 1, 1);

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin 拥有管理员角色
(2, 2); -- user 拥有普通用户角色

-- 角色菜单关联（管理员拥有所有菜单权限）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9);

-- 插入默认分类
INSERT INTO media_category (parent_id, category_name, category_code, icon, sort, status) VALUES
(0, '图片素材', 'IMAGE', 'image', 1, 1),
(0, '视频素材', 'VIDEO', 'video', 2, 1),
(0, '音频素材', 'AUDIO', 'audio', 3, 1),
(0, '文档素材', 'DOCUMENT', 'document', 4, 1),
(1, '风景图片', 'SCENERY', 'landscape', 1, 1),
(1, '人物图片', 'PEOPLE', 'people', 2, 1),
(1, '产品图片', 'PRODUCT', 'product', 3, 1),
(2, '宣传片', 'PROMOTION', 'promo', 1, 1),
(2, '教程视频', 'TUTORIAL', 'tutorial', 2, 1);

-- 插入示例素材数据
INSERT INTO media (title, description, media_type, category_id, file_path, file_name, original_name, file_size, file_ext, tags, download_count, view_count, status, is_recommend, create_by) VALUES
('蓝天白云风景图', '美丽的蓝天白云自然风景照片', 'image', 5, 'media/2024/01/01/abc123.jpg', 'abc123.jpg', 'landscape-blue-sky.jpg', 1048576, 'jpg', '风景,自然,蓝天', 100, 500, 1, 1, 1),
('城市夜景照片', '繁华都市夜景摄影作品', 'image', 5, 'media/2024/01/02/def456.jpg', 'def456.jpg', 'city-night.jpg', 2097152, 'jpg', '城市,夜景,摄影', 80, 400, 1, 1, 1),
('产品展示图片', '高清产品展示图片素材', 'image', 7, 'media/2024/01/03/ghi789.png', 'ghi789.png', 'product-show.png', 5242880, 'png', '产品,展示,商业', 150, 600, 1, 0, 1),
('企业宣传视频', '企业品牌宣传片视频素材', 'video', 8, 'media/2024/01/04/jkl012.mp4', 'jkl012.mp4', 'company-promo.mp4', 52428800, 'mp4', '企业,宣传,品牌', 50, 200, 1, 1, 1),
('音乐背景素材', '轻柔舒缓背景音乐素材', 'audio', 3, 'media/2024/01/05/mno345.mp3', 'mno345.mp3', 'background-music.mp3', 5242880, 'mp3', '音乐,背景,舒缓', 30, 150, 1, 0, 1);

-- 插入示例标签
INSERT INTO media_tag (tag_name, tag_color, use_count) VALUES
('风景', '#3498db', 10),
('自然', '#2ecc71', 8),
('城市', '#e67e22', 5),
('摄影', '#9b59b6', 7),
('产品', '#e74c3c', 3),
('商业', '#34495e', 4),
('宣传', '#f39c12', 2),
('品牌', '#1abc9c', 1);

-- ============================================
-- 创建索引
-- ============================================

CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_email ON sys_user(email);
CREATE INDEX idx_user_status ON sys_user(status);

CREATE INDEX idx_role_code ON sys_role(role_code);
CREATE INDEX idx_role_status ON sys_role(status);

CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_menu_status ON sys_menu(status);

CREATE INDEX idx_category_parent ON media_category(parent_id);
CREATE INDEX idx_category_status ON media_category(status);

CREATE INDEX idx_media_type ON media(media_type);
CREATE INDEX idx_media_category ON media(category_id);
CREATE INDEX idx_media_status ON media(status);
CREATE INDEX idx_media_recommend ON media(is_recommend);
CREATE INDEX idx_media_create_time ON media(create_time);