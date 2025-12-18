# Ruoyi货物物流库存管理系统

基于Spring Boot + MyBatis Plus开发的货物物流库存管理系统，提供仓库管理、货物管理、库存管理、出入库管理等功能。

## 技术栈

- **后端框架**：Spring Boot 2.7.18
- **ORM框架**：MyBatis Plus 3.5.3.1
- **数据库**：H2 1.4.200 (嵌入式内存数据库)
- **缓存**：无
- **构建工具**：Maven

## 项目结构

```
ruoyi-wms/
├── src/
│   ├── main/
│   │   ├── java/com/ruoyi/wms/
│   │   │   ├── controller/    # 控制器层
│   │   │   ├── domain/        # 实体类
│   │   │   ├── mapper/        # Mapper接口
│   │   │   ├── service/       # 业务逻辑层
│   │   │   │   └── impl/      # 业务实现
│   │   │   └── WmsApplication.java  # 启动类
│   │   └── resources/
│   │       ├── application.yml      # 配置文件
│   │       └── sql/init.sql         # 数据库初始化脚本
│   └── test/                   # 测试代码
├── 0.config                    # 系统配置文件
├── pom.xml                     # Maven配置
└── README.md                   # 项目说明
## 配置文件说明

### application.yml

Spring Boot应用配置文件，位于`src/main/resources/application.yml`，主要配置H2数据库连接信息。

## 数据库初始化

项目使用H2嵌入式数据库，启动时会自动执行`src/main/resources/sql/init.sql`脚本初始化数据库和测试数据，无需手动配置。

## 启动服务

### 本地直接启动

```bash
# 进入backend目录
cd backend

# 编译并启动项目
mvn spring-boot:run
```

项目启动成功后，会自动执行数据库初始化脚本，并通过以下地址访问API：
- 访问地址：http://localhost:8080/api/

## API接口说明

### 仓库管理

- `GET /api/warehouse` - 获取所有仓库
- `GET /api/warehouse/{id}` - 根据ID获取仓库
- `POST /api/warehouse` - 创建仓库
- `PUT /api/warehouse` - 更新仓库
- `DELETE /api/warehouse/{id}` - 删除仓库
- `GET /api/warehouse/name/{name}` - 根据名称查询仓库

### 货物管理

- `GET /api/goods` - 获取所有货物
- `GET /api/goods/{id}` - 根据ID获取货物
- `POST /api/goods` - 创建货物
- `PUT /api/goods` - 更新货物
- `DELETE /api/goods/{id}` - 删除货物
- `GET /api/goods/code/{code}` - 根据货物编码查询
- `GET /api/goods/type/{type}` - 根据货物类型查询

### 库存管理

- `GET /api/inventory` - 获取所有库存
- `GET /api/inventory/{id}` - 根据ID获取库存
- `GET /api/inventory/warehouse/{warehouseId}` - 根据仓库ID获取库存
- `GET /api/inventory/goods/{goodsId}` - 根据货物ID获取库存
- `POST /api/inventory` - 创建库存
- `PUT /api/inventory` - 更新库存
- `DELETE /api/inventory/{id}` - 删除库存

### 入库管理

- `GET /api/inbound` - 获取所有入库记录
- `GET /api/inbound/{id}` - 根据ID获取入库记录
- `GET /api/inbound/warehouse/{warehouseId}` - 根据仓库ID获取入库记录
- `GET /api/inbound/goods/{goodsId}` - 根据货物ID获取入库记录
- `POST /api/inbound` - 创建入库记录
- `PUT /api/inbound` - 更新入库记录
- `DELETE /api/inbound/{id}` - 删除入库记录

### 出库管理

- `GET /api/outbound` - 获取所有出库记录
- `GET /api/outbound/{id}` - 根据ID获取出库记录
- `GET /api/outbound/warehouse/{warehouseId}` - 根据仓库ID获取出库记录
- `GET /api/outbound/goods/{goodsId}` - 根据货物ID获取出库记录
- `POST /api/outbound` - 创建出库记录
- `PUT /api/outbound` - 更新出库记录
- `DELETE /api/outbound/{id}` - 删除出库记录

## 测试数据

系统初始化时会自动创建以下测试数据：

### 仓库
- 主仓库（北京市朝阳区建国路1号）
- 分仓库（上海市浦东新区陆家嘴金融中心）

### 货物
- 手机（GD001）
- 笔记本电脑（GD002）
- 鼠标（GD003）
- 键盘（GD004）

### 库存
- 主仓库：手机100台，笔记本电脑50台，鼠标200个，键盘150个
- 分仓库：手机80台，笔记本电脑30台

## 开发指南

1. 克隆项目
2. 配置开发环境（Java 8+，MySQL 8.0，Redis 7.0）
3. 执行数据库初始化脚本
4. 修改application.yml配置数据库连接
5. 启动项目进行开发

## 注意事项

1. 本系统为基础版货物物流库存管理系统，可根据实际需求进行扩展
2. 生产环境请修改默认密码，增强系统安全性
3. 建议使用Nginx作为反向代理，提高系统性能和安全性
4. 定期备份数据库，防止数据丢失

## 许可证

MIT License
