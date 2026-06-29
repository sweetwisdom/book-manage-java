你的感觉其实代表了很多刚接触测试的开发者。

因为你刚写的这个 Service：

```java
@Override
public UserVO getUserById(Long id) {
    User user = userMapper.selectById(id);
    if (user == null) {
        return null;
    }
    return convertToVO(user);
}
```

Mock测试写出来：

```java
when(userMapper.selectById(1L))
    .thenReturn(user);
```

然后断言：

```java
assertEquals("Tom", result.getName());
```

确实会让人觉得：

```text
我自己mock的数据
然后验证我自己mock的数据
好像没测什么东西
```

这个感觉没错。

---

## 你这个Demo里，Mock价值确实不大

因为业务逻辑太薄：

```text
Mapper
 ↓
Service
 ↓
返回
```

基本没有业务。

这种情况下：

```java
userMapper.selectById()
```

真正重要的是：

```sql
SQL对不对
数据库连没连上
数据查没查出来
```

而Mock把这些都绕过了。

所以：

```text
SpringBootTest + 真数据库
```

反而更有价值。

---

## Mock真正有价值的场景

假设以后变成：

```java
public UserVO createUser(UserDTO dto) {

    // 参数检查
    checkName(dto);

    // 查重
    if (userMapper.exists(dto.getName())) {
        throw new BusinessException("用户名已存在");
    }

    // 保存用户
    userMapper.insert(user);

    // 初始化积分
    pointService.initPoint(user.getId());

    // 发送欢迎邮件
    mailService.sendWelcome(user);

    // 记录日志
    logService.record(user);

    return converter.toVO(user);
}
```

这时候：

```text
依赖：
UserMapper
PointService
MailService
LogService
```

如果用真实环境测试：

```text
要数据库
要Redis
要MQ
要邮件服务器
```

很麻烦。

---

Mock后：

```java
@Mock
UserMapper userMapper;

@Mock
MailService mailService;

@Mock
PointService pointService;

@InjectMocks
UserServiceImpl userService;
```

只验证：

```text
业务逻辑是否正确
```

例如：

```java
when(userMapper.exists("Tom"))
    .thenReturn(true);

assertThrows(
    BusinessException.class,
    () -> userService.createUser(dto)
);
```

这种就很有意义。

---

## 企业里的测试金字塔

一般是：

```text
          UI测试
             ▲
             │
         集成测试
             ▲
             │
        单元测试(Mock)
```

---

### 单元测试

Mock

测试：

```text
业务逻辑
```

不测：

```text
数据库
Redis
MQ
HTTP
```

---

### 集成测试

SpringBootTest

测试：

```text
Spring
MyBatis
MySQL
Redis
```

---

### E2E测试

测试：

```text
浏览器
前端
后端
数据库
```

全链路。

---

## 国内项目的实际情况

说实话。

很多中小公司：

```text
单元测试覆盖率
≈ 0%
```

开发流程：

```text
写代码
启动项目
Swagger测一下
提交
```

结束。

---

而大厂（银行、支付、云厂商）：

```text
覆盖率要求
60%
70%
80%
```

PR不达标甚至不让合并。

因为：

```text
几十个人同时开发
频繁重构
```

没有测试很容易炸。

---

## 对你目前阶段的建议

如果你在学：

```text
SpringBoot
MyBatis Plus
RuoYi
后台开发
```

我建议优先掌握：

```text
SpringBootTest
@Test
@Transactional
```

例如：

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void createUser() {
        ...
    }
}
```

这种最有实际价值。

---

等你以后写到这种业务：

```text
支付
订单
优惠券
库存
积分
消息通知
```

再学 Mockito。

那个时候你会发现：

```text
Mock不是为了测试数据库，
而是为了隔离数据库。
```

这才是它存在的核心意义。对于你当前这个 CRUD Demo，我也会优先写 `SpringBootTest`，而不是 Mockito。
