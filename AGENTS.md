# AGENTS.md

本文件用于指导 Codex、Claude 等代码代理在本仓库中工作。除非子目录另有更具体的 `AGENTS.md`，本文件对整个项目生效。

## 项目概览

- 项目类型: Spring Boot Web 应用
- Java 版本: 11
- 构建工具: Maven
- 主包名: `com.example.bookmanage`
- 启动类: `com.example.bookmanage.BookManageApplication`
- 默认端口: `8080`
- 数据库: MySQL，连接配置在 `src/main/resources/application.yml`

## 技术栈

- Spring Boot `2.7.6`
- MyBatis-Plus `3.5.15`
- MySQL Connector/J
- Hutool `5.8.35`
- Lombok `1.18.36`
- Spring Validation
- JUnit 5 / Mockito / Spring Boot Test

## 目录约定

```text
src/main/java/com/example/bookmanage/
├── BookManageApplication.java      # 启动类
├── common/                         # 公共组件
│   ├── config/                     # 配置类
│   ├── constant/                   # 常量类
│   ├── exception/                  # 异常处理
│   ├── response/                   # 统一响应格式
│   └── utils/                      # 工具类
├── controller/                     # 控制器层，负责 HTTP 入参和响应
├── model/
│   ├── dto/                        # 请求 DTO
│   ├── entity/                     # 数据库实体
│   ├── enums/                      # 枚举
│   ├── mapper/                     # MyBatis-Plus Mapper
│   └── vo/                         # 响应 VO
└── service/                        # 业务接口与实现
```

其他目录:

- `src/main/resources/application.yml`: 应用配置。
- `src/main/resources/static/`: 静态页面资源。
- `src/test/java/`: 单元测试和集成测试。
- `doc/`: 项目学习笔记和补充文档。
- `logs/`、`target/`: 运行或构建产物，不应手动维护或提交业务变更。

## 常用命令

在仓库根目录执行:

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

注意: 当前 `spring-boot-maven-plugin` 配置了 `<skip>true</skip>`。如果打包产物需要可执行 jar，应先确认是否需要调整该配置。

## 本地配置

- MySQL 默认连接: `jdbc:mysql://localhost:3306/test`
- 默认用户名: `root`
- 密码读取: `${DB_PASSWORD:123456}`
- 不要把真实密码、令牌或个人环境配置写入 Git。
- 如需修改本地数据库密码，优先通过环境变量 `DB_PASSWORD`，不要硬编码到 `application.yml`。

## API 约定

当前基础接口:

- `GET /api/users`: 获取所有用户。
- `GET /api/users/{id}`: 根据 ID 获取用户。
- `POST /api/users`: 创建用户。

响应格式统一使用 `ApiResponse<T>`:

- 成功: `ApiResponse.success(...)`
- 失败: `ApiResponse.error(...)`

Controller 不直接返回裸对象。新增接口应保持统一响应结构，并优先通过参数校验和全局异常处理表达错误。

## 分层规则

- `controller`: 只处理 HTTP 层逻辑、参数校验、调用 service、包装响应；不要写数据库访问逻辑。
- `service`: 承载业务逻辑和事务边界；写操作需要按场景使用 `@Transactional`。
- `mapper`: 只做数据访问，优先使用 MyBatis-Plus 提供的方法。
- `dto`: 接收前端请求数据，放置 `javax.validation` 校验注解。
- `entity`: 映射数据库表结构，不混入接口展示字段。
- `vo`: 返回给前端的数据结构，不直接暴露不需要的实体字段。
- `common`: 放置跨模块复用的响应、异常、常量、配置和工具。

## 代码风格

- 保持 Java 11 兼容，不使用更高版本语法。
- 包名沿用 `com.example.bookmanage`。
- 类、方法、字段命名使用清晰的业务含义，避免无意义缩写。
- 使用 Lombok 时保持一致性；实体可使用 `@Data`，链式调用可使用 `@Accessors(chain = true)`。
- 参数校验优先使用 `@Valid`、`@NotBlank`、`@Min` 等标准注解。
- 日志使用 Lombok `@Slf4j`，不要使用 `System.out.println` 写业务日志。
- 注释只解释必要的业务意图或复杂逻辑，避免重复描述代码本身。
- 新增公共能力前先检查 `common/` 是否已有可复用实现。

## 异常处理

- 业务错误优先抛出 `BusinessException`。
- 参数校验错误由 `GlobalExceptionHandler` 统一处理。
- 不在 Controller 中拼接异常响应，统一走全局异常处理或 `ApiResponse`。
- 不吞掉异常；如果捕获异常，必须保留有用日志或转换为明确的业务异常。

## 数据访问与事务

- 优先使用 MyBatis-Plus 的 `ServiceImpl`、`BaseMapper` 和内置 CRUD。
- 查询返回给接口前应转换为 VO，不要直接返回 Entity。
- 新增或修改数据的方法根据业务一致性要求添加 `@Transactional`。
- 不在代码中写死数据库账号、密码或环境相关连接串。

## 测试要求

- 修改 service 逻辑时，优先补充或更新对应单元测试。
- 修改 controller、参数校验或异常响应时，应考虑添加 Web 层测试或集成测试。
- 测试中避免依赖本地真实数据库，除非任务明确要求集成 MySQL。
- 使用 Mockito 时，断言业务结果和关键 mapper 调用。
- 提交前至少运行与改动相关的测试；改动较大时运行 `mvn test`。

## 变更原则

- 先阅读相关现有代码，再按项目既有分层和命名方式修改。
- 保持改动范围小，避免顺手重构无关文件。
- 不修改 `target/`、`logs/` 等生成目录。
- 不提交 IDE 私有配置，除非用户明确要求。
- 不删除或回滚用户已有改动。
- 修改配置、数据库结构或 API 行为时，在最终回复中明确说明影响。

## 代理工作流程

1. 明确任务范围，优先检查 `CLAUDE.md`、`AGENTS.md`、`pom.xml` 和相关源码。
2. 使用 `rg` / `rg --files` 快速定位文件和引用。
3. 修改前确认现有模式，例如响应包装、异常处理、DTO/VO 转换和测试写法。
4. 使用最小必要改动实现需求。
5. 运行相关编译或测试命令，并在最终回复中说明结果。
6. 如果因为环境缺失无法验证，例如本地 MySQL 不可用，应明确说明未验证项和原因。

## 当前项目注意点

- `application.yml` 默认使用本地 MySQL `test` 库，运行应用前需要确认数据库可用。
- `BasicController` 的请求前缀是 `/api`，新增用户相关接口应保持路径风格一致。
- `ApiResponse` 当前成功码为 `200`，参数错误通常使用 `400`，业务或运行时错误按异常处理返回。
- `UserServiceImpl` 当前使用 Entity 到 VO 的转换方法；新增字段时要同步 DTO、Entity、VO、转换逻辑和测试。
- `doc/` 下是项目笔记，修改前确认是否属于用户正在维护的学习资料。
