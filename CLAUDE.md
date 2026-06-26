# Book Manage 项目

## 项目概述
- 类型: Spring Boot Web 应用
- Java 版本: 11
- 包名: com.example.bookmanage

## 目录结构（分层架构）
```
src/main/java/com/example/bookmanage/
├── BookManageApplication.java      # 启动类
├── common/                         # 公共组件
│   ├── config/                     # 配置类
│   ├── constant/                   # 常量类
│   ├── exception/                  # 异常处理
│   ├── response/                   # 统一响应格式
│   └── utils/                      # 工具类
├── controller/                     # 控制器层 - 接收请求
│   └── BasicController.java
├── model/                          # 数据模型层
│   ├── dto/                        # 数据传输对象 - 接收前端数据
│   │   └── UserDTO.java
│   ├── entity/                     # 实体层 - 数据库表对应
│   │   └── User.java
│   ├── enums/                      # 枚举类
│   ├── mapper/                     # 数据层 - 数据库操作
│   │   └── UserMapper.java
│   └── vo/                         # 视图对象 - 返回给前端
│       └── UserVO.java
└── service/                        # 业务层 - 业务逻辑
    ├── UserService.java            # 接口
    └── impl/
        └── UserServiceImpl.java    # 实现类
```

## 技术栈
- Spring Boot 2.7.6
- MyBatis-Plus 3.5.15
- MySQL
- Hutool 5.8.35
- Lombok 1.18.36

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
