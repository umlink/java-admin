# Java 后端转岗：第 1～3 天执行手册（超详细版）

## 🎯 总目标（3 天）

在 3 天内完成：

- Spring Boot 项目从 0 → 可运行
- MySQL / Redis 接入
- 项目基础结构搭建完成
- 公共响应结构 + 异常体系完成
- 用户模块 CRUD 打通（核心目标）

---

# 📦 一、项目结构总览（必须先理解）

```text
src/main/java/com/example/admin
├── AdminApplication.java
├── controller
│   ├── HealthController.java
│   └── UserController.java
├── service
│   └── UserService.java
├── service/impl
│   └── UserServiceImpl.java
├── mapper
│   └── SysUserMapper.java
├── entity
│   └── SysUser.java
├── dto
│   ├── UserCreateDTO.java
│   └── UserUpdateDTO.java
├── vo
│   └── UserVO.java
├── config
│   └── MybatisPlusConfig.java
├── common/api
│   ├── Result.java
│   └── ResultCode.java
├── exception
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
└── utils
    └── PasswordUtil.java
```

# 二、实施目标

- 项目能启动 
- /ping 可访问 
- 包结构完整
- 数据库接入
- entity / mapper 完成
- 公共结构完成
- MySQL 可连接
- Mapper 可用
- entity 正确
- Result 可返回
- 用户模块 CRUD 块完整打通

## 验收标准

- 用户新增成功
- 用户查询成功 
- 有数据 
- 接口正常返回 JSON


