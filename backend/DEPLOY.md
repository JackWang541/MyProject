# 货物物流库存管理系统部署指南

## 环境要求

### 1. Java 8+

### 2. Maven 3.6+

## 安装步骤

### 1. 安装Java 8

#### macOS
```bash
# 使用Homebrew安装Java 8
brew tap adoptopenjdk/openjdk
brew install adoptopenjdk8

# 验证安装
java -version
```

#### Linux
```bash
# Ubuntu/Debian
apt-get update
apt-get install -y openjdk-8-jdk

# CentOS/RHEL
yum install -y java-1.8.0-openjdk

# 验证安装
java -version
```

#### Windows
1. 下载Java 8安装包：https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
2. 双击安装，按照向导完成安装
3. 配置环境变量
   - JAVA_HOME: 指向JDK安装目录
   - PATH: 添加%JAVA_HOME%\bin
4. 验证安装：打开命令提示符，输入`java -version`

### 2. 安装Maven

#### macOS
```bash
# 使用Homebrew安装Maven
brew install maven

# 验证安装
mvn -version
```

#### Linux
```bash
# Ubuntu/Debian
apt-get update
apt-get install -y maven

# CentOS/RHEL
yum install -y maven

# 验证安装
mvn -version
```

#### Windows
1. 下载Maven：https://maven.apache.org/download.cgi
2. 解压到安装目录，如：C:\Program Files\Apache\maven
3. 配置环境变量
   - MAVEN_HOME: 指向Maven安装目录
   - PATH: 添加%MAVEN_HOME%\bin
4. 验证安装：打开命令提示符，输入`mvn -version`

## 数据库初始化

项目使用H2嵌入式数据库，启动时会自动执行`src/main/resources/sql/init.sql`脚本初始化数据库和测试数据，无需手动配置。

## 项目配置

项目默认使用H2嵌入式数据库，`src/main/resources/application.yml`文件已配置好H2数据库连接信息：

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:wms_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2-console
```

## 编译项目

```bash
mvn clean package -DskipTests
```

编译成功后，会在`target`目录下生成`ruoyi-wms-1.0.0.jar`文件。

## 运行项目

### 方式一：使用Maven直接运行（推荐）

```bash
mvn spring-boot:run
```

### 方式二：使用jar包运行

```bash
java -jar target/ruoyi-wms-1.0.0.jar
```

项目启动成功后，可以通过以下地址访问API：
- 访问地址：http://localhost:8080/api/
- H2数据库控制台：http://localhost:8080/h2-console (用户名：sa，密码：空，JDBC URL：jdbc:h2:mem:wms_db)

## API测试

可以使用Postman或curl工具测试API：

### 1. 获取所有仓库
```bash
curl http://localhost:8080/api/warehouse
```

### 2. 获取所有货物
```bash
curl http://localhost:8080/api/goods
```

### 3. 获取所有库存
```bash
curl http://localhost:8080/api/inventory
```

## 常见问题

### 1. 端口被占用

如果8080端口被占用，可以修改`application.yml`文件中的端口号：

```yaml
server:
  port: 8081
```

### 2. Maven编译失败

检查Maven是否正确安装，以及网络连接是否正常。

## 技术支持

如果在部署过程中遇到问题，可以参考以下资源：

1. Spring Boot官方文档：https://docs.spring.io/spring-boot/docs/current/reference/html/
2. MyBatis Plus官方文档：https://baomidou.com/pages/24112f/
3. MySQL官方文档：https://dev.mysql.com/doc/
4. Redis官方文档：https://redis.io/documentation

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
├── DEPLOY.md                   # 部署指南
├── Dockerfile                  # Dockerfile
├── README.md                   # 项目说明
├── docker-compose.yml          # Docker Compose配置
└── pom.xml                     # Maven配置
```

## 许可证

MIT License
