# Java 后端转岗：第 4～5 天执行手册（登录鉴权与 Redis）

## 🎯 总目标（2 天）

在 2 天内完成：

- Redis 接入与配置
- Sa-Token 登录鉴权框架集成
- 登录 / 登出接口
- Token 鉴权拦截
- 用户分页与条件筛选
- Swagger API 文档接入

---

## 📦 一、项目结构新增

```text
src/main/java/com/example/admin
├── config
│   ├── RedisConfig.java (新增)
│   └── SaTokenConfig.java (新增)
├── utils
│   └── RedisUtil.java (新增)
├── dto
│   ├── LoginDTO.java (新增)
│   └── UserQueryDTO.java (新增)
└── vo
    └── LoginVO.java (新增)
```

---

## 二、第 4 天目标：Redis 接入与登录接口

### 核心动作

1. **Redis 接入**
   - pom.xml 引入 `spring-boot-starter-data-redis`
   - 配置 `application.yaml` Redis 连接
   - 创建 `RedisConfig` 配置类
   - 创建 `RedisUtil` 工具类

2. **Sa-Token 集成**
   - pom.xml 引入 `sa-token-spring-boot3-starter`
   - 配置 `SaTokenConfig`
   - 配置 Sa-Token 使用 Redis 存储 Token

3. **登录功能**
   - 创建 `LoginDTO`（用户名 + 密码）
   - 创建 `LoginVO`（token + 用户信息）
   - 在 `SysUserService` 新增 `login()` 方法
   - 在 `SysUserController` 新增登录接口 `/api/auth/login`
   - 密码校验逻辑
   - 登录成功生成 Token

4. **用户分页查询**
   - 创建 `UserQueryDTO`（用户名、状态、分页参数）
   - 在 `SysUserService` 新增 `page()` 方法
   - 支持按用户名模糊查询
   - 支持按状态筛选

### 验收标准

- Redis 连接成功
- 登录接口返回 Token
- 使用 Token 可以访问需要鉴权的接口
- 用户分页查询正常

---

## 三、第 5 天目标：登出与权限基础完善

### 核心动作

1. **登出功能**
   - 在 `SysUserController` 新增登出接口 `/api/auth/logout`
   - Token 注销

2. **获取当前用户信息**
   - 新增 `/api/auth/current` 接口
   - 返回当前登录用户详情

3. **Swagger API 文档**
   - pom.xml 引入 `springdoc-openapi-starter-webmvc-ui`
   - 配置 SpringDoc
   - 访问 `/swagger-ui/index.html` 查看文档

4. **参数校验完善**
   - 给所有 DTO 加上 `@Validated`
   - 完善校验注解：`@NotBlank`、`@Size`、`@Pattern` 等

5. **测试验证**
   - 用 Swagger 测试所有接口
   - 登录 → 获取用户列表 → 登出 流程完整

### 验收标准

- 登出后 Token 失效
- 当前用户接口正常
- Swagger 文档可访问
- 所有接口参数校验生效

---

## 四、重点学习点

1. **Redis 基础**
   - String 类型存储 Token
   - Redis 连接池配置
   - 序列化方式选择

2. **Sa-Token 核心**
   - `StpUtil.login()` 登录
   - `StpUtil.getTokenValue()` 获取 Token
   - `StpUtil.checkLogin()` 校验登录
   - `StpUtil.logout()` 登出

3. **分页查询**
   - MyBatis-Plus `Page` 对象
   - `QueryWrapper` 条件构造
   - 模糊查询 `like()`
   - 精确查询 `eq()`

---

## 五、面试点整理

1. Redis 为什么快？
2. Token  vs Session 区别？
3. 为什么用 Sa-Token 而不是 Shiro / Spring Security？
4. MyBatis-Plus 分页插件原理？
5. 如何防止 Token 被劫持？