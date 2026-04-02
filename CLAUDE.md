# 项目协作规范

技术栈：JDK 17 / Spring Boot 3.4.2 / MyBatis-Plus 3.5.8 / Sa-Token 1.37.0(JWT) / Redis / MySQL 8 / Flyway
构建：统一使用 `./mvnw`，编译验证 `./mvnw clean compile -q`
配置文件：`src/main/resources/application.yaml`（注意是 .yaml 不是 .yml）

---

## 核心约束

**分层**
- Controller 只做参数接收 + 调用 Service + 返回响应，禁止写业务逻辑
- 使用 `@RequiredArgsConstructor + final` 字段注入，禁止 `@Autowired` 字段注入
- 禁止接口层直接暴露 Entity，必须转换为 VO
- 事务放在 Service 层

**分页规范**
- 分页参数统一：`pageNum`（默认1）/ `pageSize`（默认10）
- 查询参数 > 1 个的分页接口：`POST + @RequestBody QueryDTO`
- 仅有分页参数的简单查询可用 GET

**审计字段**
- `createdAt/updatedAt`：MySQL 层自动维护，代码层不赋值
- `createdBy/updatedBy`：由 `AuditMetaObjectHandler` 从 Sa-Token 填充，Service 层不赋值

**权限**
- 所有管理接口必须加 `@SaCheckPermission`
- 权限码统一在 `PermissionConstants` 中定义
- 超管角色 `ADMIN` 返回 `*`，无需维护数据库权限记录

**数据库变更**
- 所有表结构变更通过 Flyway 脚本管理（`src/main/resources/db/migration/`），命名格式 `V{n}__{desc}.sql`
- 变更前说明：是否影响已有数据、是否需要索引调整、是否存在锁表风险

**Redis**
- 新增缓存必须说明：key 设计、TTL、更新策略、一致性风险
- key 命名：`业务域:对象类型:主键`（如 `perm:user:1`）
- 修改数据库逻辑时同步评估缓存失效

**禁止事项**
- 禁止 `import xxx.*`
- 禁止未经说明升级依赖版本
- 禁止未经说明修改公共接口返回结构
- 禁止在日志输出密码、token、敏感信息
- 禁止硬编码密钥、密码、证书

---

## 优先级

正确性 > 安全性 > 数据一致性 > 可维护性 > 性能 > 开发速度
