# Spring Boot 异常处理指南

## 概述

本文档介绍如何在 Spring Boot 项目中实现统一的异常处理机制，包括：
- 全局异常处理器
- 自定义业务异常
- 参数校验异常处理
- 日志记录

## 架构设计

### 目录结构

```
src/main/java/com/example/bookmanage/common/exception/
├── BusinessException.java        # 自定义业务异常
└── GlobalExceptionHandler.java   # 全局异常处理器
```

### 异常处理流程

```
Controller
    ↓
Service
    ↓ 抛出异常
GlobalExceptionHandler
    ↓ 捕获并处理
统一响应格式 (ApiResponse)
    ↓
前端
```

## 核心组件

### 1. 统一响应格式 (ApiResponse)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;      // 状态码
    private String message; // 提示信息
    private T data;        // 响应数据

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
```

### 2. 自定义业务异常 (BusinessException)

```java
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
```

### 3. 全局异常处理器 (GlobalExceptionHandler)

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常处理
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    // 参数校验异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(400, message);
    }

    // 运行时异常处理
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return ApiResponse.error(500, e.getMessage());
    }
}
```

## 异常类型与处理策略

### 1. 业务异常 (BusinessException)

**场景**：业务逻辑错误（如用户不存在、余额不足等）

**日志级别**：WARN

**示例**：
```java
throw new BusinessException("用户不存在");
throw new BusinessException(404, "用户不存在");
```

### 2. 参数校验异常 (MethodArgumentNotValidException)

**场景**：请求参数校验失败（如必填字段为空、格式错误等）

**日志级别**：WARN

**使用方式**：
```java
// DTO 中定义校验规则
public class UserDTO {
    @NotBlank(message = "用户名不能为空")
    private String name;
    
    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;
}

// Controller 中启用校验
@PostMapping("/users")
public ApiResponse<UserVO> createUser(@Valid @RequestBody UserDTO userDTO) {
    return ApiResponse.success(userService.createUser(userDTO));
}
```

### 3. 运行时异常 (RuntimeException)

**场景**：未预期的系统错误（如空指针、数据库连接失败等）

**日志级别**：ERROR（包含完整堆栈信息）

## 日志配置

### 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <parameter name="artifactId">spring-boot-starter-validation</artifactId>
</dependency>
```

### application.yml

```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    com.example.bookmanage.common.exception: DEBUG
```

## 最佳实践

### 1. 异常分层

- **Controller 层**：捕获并处理参数异常
- **Service 层**：抛出业务异常
- **Mapper 层**：抛出数据访问异常

### 2. 日志级别规范

| 异常类型 | 日志级别 | 说明 |
|----------|----------|------|
| 业务异常 | WARN | 预期的业务错误 |
| 参数校验 | WARN | 用户输入错误 |
| 运行时异常 | ERROR | 未预期的系统错误 |
| 系统异常 | ERROR | 需要立即处理 |

### 3. 异常信息规范

```java
// ✅ 好的实践
throw new BusinessException("用户名不能为空");
throw new BusinessException(404, "用户不存在");

// ❌ 避免的做法
throw new BusinessException("Error");
throw new BusinessException(e.getMessage());
```

## 常见问题

### Q1: 为什么用 `@RestControllerAdvice` 而不是 `@ControllerAdvice`？

`@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`，会自动将返回值序列化为 JSON。

### Q2: 为什么用 `?` 泛型？

```java
public ApiResponse<?> handleBusinessException(BusinessException e) {
    return ApiResponse.error(e.getCode(), e.getMessage());
}
```

`?` 表示任意类型，因为异常处理器需要处理各种不同的业务场景，每种情况返回的 `data` 类型可能不同。

### Q3: 如何处理自定义异常？

1. 创建自定义异常类继承 `RuntimeException`
2. 在 `GlobalExceptionHandler` 中添加对应的处理方法
3. 使用 `@ExceptionHandler` 注解指定异常类型

## 参考资料

- [Spring Boot 官方文档 - 异常处理](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-error-handling)
- [Spring MVC 异常处理](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#mvc-exceptionhandlers)

---

*最后更新：2026-06-25*
