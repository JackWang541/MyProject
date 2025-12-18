-- 仓库表
CREATE TABLE IF NOT EXISTS wms_warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    address VARCHAR(255) COMMENT '仓库地址',
    manager VARCHAR(50) COMMENT '仓库管理员',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 0 COMMENT '状态：0启用，1禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_warehouse_name (warehouse_name)
) COMMENT '仓库表';

-- 货物表
CREATE TABLE IF NOT EXISTS wms_goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_code VARCHAR(50) NOT NULL COMMENT '货物编码',
    goods_name VARCHAR(100) NOT NULL COMMENT '货物名称',
    goods_type VARCHAR(50) COMMENT '货物类型',
    unit VARCHAR(20) NOT NULL COMMENT '计量单位',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    description TEXT COMMENT '货物描述',
    status TINYINT DEFAULT 0 COMMENT '状态：0启用，1禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_goods_code (goods_code)
) COMMENT '货物表';

-- 库存表
CREATE TABLE IF NOT EXISTS wms_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    goods_id BIGINT NOT NULL COMMENT '货物ID',
    quantity INT DEFAULT 0 COMMENT '库存数量',
    safe_quantity INT DEFAULT 0 COMMENT '安全库存',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_inventory_warehouse_goods (warehouse_id, goods_id),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id) ON DELETE CASCADE,
    FOREIGN KEY (goods_id) REFERENCES wms_goods(id) ON DELETE CASCADE
) COMMENT '库存表';

-- 入库记录表
CREATE TABLE IF NOT EXISTS wms_inbound_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_no VARCHAR(50) NOT NULL COMMENT '入库单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    goods_id BIGINT NOT NULL COMMENT '货物ID',
    quantity INT NOT NULL COMMENT '入库数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '总金额',
    supplier VARCHAR(100) COMMENT '供应商',
    operator VARCHAR(50) COMMENT '操作员',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_inbound_record_no (record_no),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id),
    FOREIGN KEY (goods_id) REFERENCES wms_goods(id)
) COMMENT '入库记录表';

-- 出库记录表
CREATE TABLE IF NOT EXISTS wms_outbound_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_no VARCHAR(50) NOT NULL COMMENT '出库单号',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    goods_id BIGINT NOT NULL COMMENT '货物ID',
    quantity INT NOT NULL COMMENT '出库数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '总金额',
    customer VARCHAR(100) COMMENT '客户',
    operator VARCHAR(50) COMMENT '操作员',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_outbound_record_no (record_no),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id),
    FOREIGN KEY (goods_id) REFERENCES wms_goods(id)
) COMMENT '出库记录表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(50) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    type VARCHAR(20) DEFAULT 'page' COMMENT '权限类型：page-页面, button-按钮',
    path VARCHAR(255) COMMENT '访问路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    parent_id BIGINT DEFAULT 0 COMMENT '父级权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_permission_code (permission_code)
) COMMENT '权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(20) NOT NULL COMMENT '角色标识',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_permission (role, permission_id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) COMMENT '角色权限关联表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    ip VARCHAR(50) COMMENT '登录IP',
    user_agent VARCHAR(255) COMMENT '浏览器信息',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    status TINYINT NOT NULL COMMENT '登录状态：0失败，1成功',
    message VARCHAR(255) COMMENT '登录消息'
) COMMENT '登录日志表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    role VARCHAR(20) NOT NULL COMMENT '角色：root, admin, user',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_time DATETIME COMMENT '最后登录时间',
    UNIQUE KEY uk_username (username)
) COMMENT '用户表';

-- 插入测试数据
INSERT INTO wms_warehouse (warehouse_name, address, manager, phone, status) VALUES
('主仓库', '北京市朝阳区建国路1号', '张三', '13800138001', 0),
('分仓库', '上海市浦东新区陆家嘴金融中心', '李四', '13900139001', 0),
('华南仓库', '广州市天河区天河路385号', '王五', '13700137001', 0),
('西南仓库', '成都市高新区天府大道北段1480号', '赵六', '13600136001', 0),
('西北仓库', '西安市雁塔区科技路37号', '孙七', '13500135001', 0);

INSERT INTO wms_goods (goods_code, goods_name, goods_type, unit, price, description, status) VALUES
('GD001', '手机', '电子产品', '台', 5999.00, '智能手机', 0),
('GD002', '笔记本电脑', '电子产品', '台', 7999.00, '高性能笔记本', 0),
('GD003', '鼠标', '电脑配件', '个', 99.00, '无线鼠标', 0),
('GD004', '键盘', '电脑配件', '个', 199.00, '机械键盘', 0),
('GD005', '显示器', '电脑配件', '台', 1999.00, '27英寸4K显示器', 0),
('GD006', '打印机', '办公设备', '台', 2999.00, '激光打印机', 0),
('GD007', '投影仪', '办公设备', '台', 3999.00, '高清投影仪', 0),
('GD008', '耳机', '音频设备', '副', 899.00, '无线降噪耳机', 0),
('GD009', '音箱', '音频设备', '个', 1299.00, '蓝牙音箱', 0),
('GD010', '摄像头', '电脑配件', '个', 499.00, '高清摄像头', 0);

INSERT INTO wms_inventory (warehouse_id, goods_id, quantity, safe_quantity) VALUES
(1, 1, 100, 10),
(1, 2, 50, 5),
(1, 3, 200, 20),
(1, 4, 150, 15),
(1, 5, 80, 8),
(1, 6, 40, 4),
(1, 7, 30, 3),
(1, 8, 120, 12),
(1, 9, 90, 9),
(1, 10, 180, 18),
(2, 1, 80, 8),
(2, 2, 30, 3),
(2, 3, 150, 15),
(2, 4, 120, 12),
(2, 5, 60, 6),
(3, 1, 60, 6),
(3, 2, 40, 4),
(3, 3, 200, 20),
(3, 4, 180, 18),
(3, 5, 100, 10),
(4, 1, 70, 7),
(4, 2, 50, 5),
(4, 3, 160, 16),
(4, 4, 140, 14),
(4, 5, 90, 9),
(5, 1, 50, 5),
(5, 2, 35, 3),
(5, 3, 180, 18),
(5, 4, 160, 16),
(5, 5, 80, 8);

-- 插入测试入库记录
INSERT INTO wms_inbound_record (record_no, warehouse_id, goods_id, quantity, unit_price, total_amount, supplier, operator, remark) VALUES
('RK202512180001', 1, 1, 50, 5999.00, 299950.00, '供应商A', '张三', '批量采购手机'),
('RK202512180002', 1, 2, 20, 7999.00, 159980.00, '供应商B', '张三', '采购笔记本电脑'),
('RK202512180003', 2, 1, 30, 5899.00, 176970.00, '供应商A', '李四', '分仓库补货手机'),
('RK202512180004', 1, 3, 100, 99.00, 9900.00, '供应商C', '张三', '采购鼠标'),
('RK202512180005', 1, 4, 50, 199.00, 9950.00, '供应商D', '张三', '采购键盘'),
('RK202512180006', 3, 1, 40, 5999.00, 239960.00, '供应商A', '王五', '华南仓库采购手机'),
('RK202512180007', 3, 2, 25, 7899.00, 197475.00, '供应商B', '王五', '华南仓库采购笔记本电脑'),
('RK202512180008', 4, 1, 35, 5999.00, 209965.00, '供应商A', '赵六', '西南仓库采购手机'),
('RK202512180009', 4, 2, 22, 7999.00, 175978.00, '供应商B', '赵六', '西南仓库采购笔记本电脑'),
('RK202512180010', 5, 1, 30, 5999.00, 179970.00, '供应商A', '孙七', '西北仓库采购手机'),
('RK202512180011', 5, 2, 18, 7999.00, 143982.00, '供应商B', '孙七', '西北仓库采购笔记本电脑'),
('RK202512180012', 1, 5, 40, 1899.00, 75960.00, '供应商E', '张三', '采购显示器'),
('RK202512180013', 1, 6, 20, 2899.00, 57980.00, '供应商F', '张三', '采购打印机'),
('RK202512180014', 2, 5, 30, 1899.00, 56970.00, '供应商E', '李四', '分仓库采购显示器'),
('RK202512180015', 2, 6, 15, 2899.00, 43485.00, '供应商F', '李四', '分仓库采购打印机');

-- 插入测试出库记录
INSERT INTO wms_outbound_record (record_no, warehouse_id, goods_id, quantity, unit_price, total_amount, customer, operator, remark) VALUES
('CK202512180001', 1, 1, 20, 6299.00, 125980.00, '客户A', '张三', '销售手机给客户A'),
('CK202512180002', 1, 2, 10, 8299.00, 82990.00, '客户B', '张三', '销售笔记本电脑给客户B'),
('CK202512180003', 2, 1, 15, 6199.00, 92985.00, '客户C', '李四', '分仓库销售手机'),
('CK202512180004', 1, 3, 50, 109.00, 5450.00, '客户D', '张三', '销售鼠标'),
('CK202512180005', 1, 4, 30, 219.00, 6570.00, '客户E', '张三', '销售键盘'),
('CK202512180006', 3, 1, 25, 6299.00, 157475.00, '客户F', '王五', '华南仓库销售手机'),
('CK202512180007', 3, 2, 12, 8299.00, 99588.00, '客户G', '王五', '华南仓库销售笔记本电脑'),
('CK202512180008', 4, 1, 18, 6299.00, 113382.00, '客户H', '赵六', '西南仓库销售手机'),
('CK202512180009', 4, 2, 9, 8299.00, 74691.00, '客户I', '赵六', '西南仓库销售笔记本电脑'),
('CK202512180010', 5, 1, 12, 6299.00, 75588.00, '客户J', '孙七', '西北仓库销售手机'),
('CK202512180011', 5, 2, 7, 8299.00, 58093.00, '客户K', '孙七', '西北仓库销售笔记本电脑'),
('CK202512180012', 1, 5, 30, 2099.00, 62970.00, '客户L', '张三', '销售显示器'),
('CK202512180013', 1, 6, 15, 3199.00, 47985.00, '客户M', '张三', '销售打印机'),
('CK202512180014', 2, 5, 22, 2099.00, 46178.00, '客户N', '李四', '分仓库销售显示器'),
('CK202512180015', 2, 6, 10, 3199.00, 31990.00, '客户O', '李四', '分仓库销售打印机');

-- 更新库存数量，模拟入库出库后的实际库存
UPDATE wms_inventory SET quantity = 130 WHERE warehouse_id = 1 AND goods_id = 1;
UPDATE wms_inventory SET quantity = 60 WHERE warehouse_id = 1 AND goods_id = 2;
UPDATE wms_inventory SET quantity = 250 WHERE warehouse_id = 1 AND goods_id = 3;
UPDATE wms_inventory SET quantity = 170 WHERE warehouse_id = 1 AND goods_id = 4;
UPDATE wms_inventory SET quantity = 90 WHERE warehouse_id = 1 AND goods_id = 5;
UPDATE wms_inventory SET quantity = 45 WHERE warehouse_id = 1 AND goods_id = 6;
UPDATE wms_inventory SET quantity = 30 WHERE warehouse_id = 1 AND goods_id = 7;
UPDATE wms_inventory SET quantity = 120 WHERE warehouse_id = 1 AND goods_id = 8;
UPDATE wms_inventory SET quantity = 90 WHERE warehouse_id = 1 AND goods_id = 9;
UPDATE wms_inventory SET quantity = 180 WHERE warehouse_id = 1 AND goods_id = 10;
UPDATE wms_inventory SET quantity = 95 WHERE warehouse_id = 2 AND goods_id = 1;
UPDATE wms_inventory SET quantity = 38 WHERE warehouse_id = 2 AND goods_id = 2;
UPDATE wms_inventory SET quantity = 200 WHERE warehouse_id = 2 AND goods_id = 3;
UPDATE wms_inventory SET quantity = 140 WHERE warehouse_id = 2 AND goods_id = 4;
UPDATE wms_inventory SET quantity = 68 WHERE warehouse_id = 2 AND goods_id = 5;
UPDATE wms_inventory SET quantity = 45 WHERE warehouse_id = 2 AND goods_id = 6;
UPDATE wms_inventory SET quantity = 75 WHERE warehouse_id = 3 AND goods_id = 1;
UPDATE wms_inventory SET quantity = 48 WHERE warehouse_id = 3 AND goods_id = 2;
UPDATE wms_inventory SET quantity = 200 WHERE warehouse_id = 3 AND goods_id = 3;
UPDATE wms_inventory SET quantity = 180 WHERE warehouse_id = 3 AND goods_id = 4;
UPDATE wms_inventory SET quantity = 100 WHERE warehouse_id = 3 AND goods_id = 5;
UPDATE wms_inventory SET quantity = 87 WHERE warehouse_id = 4 AND goods_id = 1;
UPDATE wms_inventory SET quantity = 61 WHERE warehouse_id = 4 AND goods_id = 2;
UPDATE wms_inventory SET quantity = 160 WHERE warehouse_id = 4 AND goods_id = 3;
UPDATE wms_inventory SET quantity = 140 WHERE warehouse_id = 4 AND goods_id = 4;
UPDATE wms_inventory SET quantity = 90 WHERE warehouse_id = 4 AND goods_id = 5;
UPDATE wms_inventory SET quantity = 68 WHERE warehouse_id = 5 AND goods_id = 1;
UPDATE wms_inventory SET quantity = 41 WHERE warehouse_id = 5 AND goods_id = 2;
UPDATE wms_inventory SET quantity = 180 WHERE warehouse_id = 5 AND goods_id = 3;
UPDATE wms_inventory SET quantity = 160 WHERE warehouse_id = 5 AND goods_id = 4;
UPDATE wms_inventory SET quantity = 80 WHERE warehouse_id = 5 AND goods_id = 5;

-- 初始化用户数据
-- 用户密码：admin123456（明文）
INSERT INTO sys_user (username, password, nickname, email, phone, status, role) VALUES
('root', 'admin123456', '系统管理员', 'root@example.com', '13800138000', 1, 'root'),
('admin', 'admin123456', '管理员', 'admin@example.com', '13800138001', 1, 'admin'),
('user', 'admin123456', '普通用户', 'user@example.com', '13800138002', 1, 'user');

-- 初始化权限数据
INSERT INTO sys_permission (permission_code, permission_name, type, path, component, icon, parent_id, sort_order) VALUES
('SYSTEM_MENU', '系统管理', 'page', '/system', null, 'Setting', 0, 1),
('USER_MANAGEMENT', '用户管理', 'page', '/system/user', 'views/system/User.vue', 'User', 1, 1),
('ROLE_MANAGEMENT', '角色管理', 'page', '/system/role', 'views/system/Role.vue', 'UserFilled', 1, 2),
('LOG_MANAGEMENT', '登录日志', 'page', '/system/log', 'views/system/LoginLog.vue', 'Document', 1, 3),
('WAREHOUSE_MANAGEMENT', '仓库管理', 'page', '/warehouse', 'views/Warehouse.vue', 'Location', 0, 2),
('GOODS_MANAGEMENT', '货物管理', 'page', '/goods', 'views/Goods.vue', 'Box', 0, 3),
('INVENTORY_MANAGEMENT', '库存管理', 'page', '/inventory', 'views/Inventory.vue', 'DataAnalysis', 0, 4),
('INBOUND_MANAGEMENT', '入库管理', 'page', '/inbound', 'views/Inbound.vue', 'ArrowDown', 0, 5),
('OUTBOUND_MANAGEMENT', '出库管理', 'page', '/outbound', 'views/Outbound.vue', 'ArrowUp', 0, 6),
('PROFILE_MANAGEMENT', '个人中心', 'page', '/profile', 'views/Profile.vue', 'User', 0, 7);

-- 初始化角色权限关联数据
-- root角色拥有所有权限
INSERT INTO sys_role_permission (role, permission_id) VALUES
('root', 1),
('root', 2),
('root', 3),
('root', 4),
('root', 5),
('root', 6),
('root', 7),
('root', 8),
('root', 9),
('root', 10);

-- admin角色拥有大部分权限，除了系统管理下的用户和角色管理
INSERT INTO sys_role_permission (role, permission_id) VALUES
('admin', 5),
('admin', 6),
('admin', 7),
('admin', 8),
('admin', 9),
('admin', 10);

-- user角色拥有基础权限
INSERT INTO sys_role_permission (role, permission_id) VALUES
('user', 8),
('user', 9),
('user', 10);
