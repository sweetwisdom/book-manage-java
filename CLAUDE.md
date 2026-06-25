# Book Manage 项目

## 项目概述
- 类型: Spring Boot Web 应用
- Java 版本: 11
- 包名: com.example.bookmanage

## 目录结构（分层架构）
```
src/main/java/com/example/bookmanage/
├── BookManageApplication.java      # 启动类
├── controller/                     # 控制器层 - 接收请求
│   └── BasicController.java
├── service/                        # 业务层 - 业务逻辑
│   └── UserService.java
├── mapper/                         # 数据层 - 数据库操作
│   └── UserMapper.java
├── entity/                         # 实体层 - 数据库表对应
│   └── User.java
├── dto/                            # 数据传输对象 - 接收前端数据
│   └── UserDTO.java
└── vo/                             # 视图对象 - 返回给前端
    └── UserVO.java
```

## 技术栈
- Spring Boot 2.7.6
- MyBatis 2.3.1
- MySQL
- Hutool 5.8.35

## 常用命令
```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run

# 测试
mvn test

# 打包
mvn clean package
```

## API 端点
- GET /api/users - 获取所有用户
- GET /api/users/{id} - 根据ID获取用户
- POST /api/users - 创建用户
