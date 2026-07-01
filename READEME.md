# Book Manage

Book Manage 是一个基于 Spring Boot 的图书管理系统示例项目，当前已实现用户相关基础接口，并为后续图书、分类、库存、借阅记录等业务模块预留了项目结构和数据库设计文档。

技术细节和代码代理协作规范请参考 [AGENTS.md](./AGENTS.md)。

## 项目概览

- 项目类型：Spring Boot Web 应用
- Java 版本：11
- 构建工具：Maven
- 默认端口：8080
- 数据库：MySQL
- 数据库迁移：Flyway
- 主包名：`com.example.bookmanage`
- 启动类：`com.example.bookmanage.BookManageApplication`

## 技术栈

- Spring Boot 2.7.6
- MyBatis-Plus 3.5.15
- MySQL Connector/J
- Flyway
- Spring Validation
- Hutool 5.8.35
- Lombok 1.18.36
- JUnit 5 / Mockito / Spring Boot Test

## 目录结构

```text
src/main/java/com/example/bookmanage/
├── BookManageApplication.java      # 启动类
├── common/                         # 公共组件
│   ├── config/                     # 配置类
│   ├── exception/                  # 异常处理
│   └── response/                   # 统一响应
├── controller/                     # 控制器层
├── model/
│   ├── dto/                        # 请求 DTO
│   ├── entity/                     # 数据库实体
│   ├── mapper/                     # MyBatis-Plus Mapper
│   └── vo/                         # 响应 VO
└── service/                        # 业务接口与实现
```

其他重要目录：

- `src/main/resources/application.yml`：应用配置
- `src/main/resources/db/migration/`：Flyway 数据库迁移脚本
- `src/main/resources/static/`：静态页面资源
- `src/test/java/`：测试代码
- `doc/`：项目文档和学习笔记

## 本地环境

启动前请确认本机已安装：

- JDK 11
- Maven
- MySQL

默认数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: ${DB_PASSWORD:123456}
```

建议通过环境变量设置数据库密码：

```bash
set DB_PASSWORD=your_password
```

PowerShell 可使用：

```powershell
$env:DB_PASSWORD="your_password"
```

## 数据库迁移

项目使用 Flyway 管理数据库结构变更。

当前迁移脚本位置：

```text
src/main/resources/db/migration/
```

已有初始化脚本：

```text
V1__init_schema.sql
```

新增数据库结构时，请新增更高版本的迁移脚本，例如：

```text
V2__create_book_tables.sql
```

不要直接修改已经提交并在环境中执行过的历史迁移脚本。

## 常用命令

在项目根目录执行：

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run

# 打包
mvn clean package
```

注意：当前 `spring-boot-maven-plugin` 配置了 `<skip>true</skip>`。如果需要生成可执行 jar，需要先确认是否调整该配置。

## 当前接口

当前基础接口前缀为 `/api`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/users` | 分页查询用户 |
| `GET` | `/api/users/{id}` | 根据 ID 获取用户 |
| `POST` | `/api/users` | 创建用户 |

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "traceId": "",
  "timestamp": 1710000000000
}
```

## 示例请求

分页查询用户：

```bash
curl "http://localhost:8080/api/users?pageNum=1&pageSize=10"
```

根据 ID 查询用户：

```bash
curl "http://localhost:8080/api/users/1"
```

创建用户：

```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Tom\",\"age\":18}"
```

## 项目文档

- [AGENTS.md](./AGENTS.md)：代码代理协作规范和项目技术约定
- [数据库设计文档](./doc/数据库设计.md)：图书管理系统数据库设计
- [异常处理文档](./doc/异常处理/exception-handling.md)：异常处理相关说明

## 开发约定

- Controller 统一返回 `ApiResponse<T>`，不直接返回裸对象。
- 请求参数优先使用 DTO，并通过 `@Valid` 和校验注解进行校验。
- Service 承载业务逻辑，写操作按场景使用 `@Transactional`。
- 数据库访问优先使用 MyBatis-Plus 的 `ServiceImpl`、`BaseMapper` 和内置 CRUD。
- 数据库结构变更必须通过 Flyway 迁移脚本管理。
- 返回给前端的数据使用 VO，不直接暴露 Entity。
- 修改 service、controller、参数校验或异常响应时，应同步补充相关测试。

## 后续规划

项目后续可以按以下顺序扩展：

1. 图书分类管理
2. 图书管理
3. 图书库存管理
4. 借阅和归还流程
5. 借阅规则配置
6. 图书操作日志

