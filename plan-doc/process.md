# AI 任务执行情况记录

## 更新时间
- 2026-04-01（UTC+8，最新一次核对 Play-01 状态）

## 大计划进度
- **Play-01 权限后台基础（第 1～3 天）**：✅ 已完成。工程脚手架、数据库/Redis 接入、统一响应+异常、用户/角色/菜单 CRUD 及关联关系、登录基础链路全部可用。
- **Play-02 登录鉴权与 Redis（第 4～5 天）**：🔄 进行中。已完成登录/登出/当前用户接口、Sa-Token 拦截器、Redis 配置与工具类、Knife4j 接入与 DTO 校验；剩余工作集中在 Token 持久化、查询筛选与安全收尾。
- **Play-03 权限分配与缓存设计**：⏳ 待启动，等 Play-02 完成并补齐安全项后进入。
- **Play-04 及以后**：未启动。

## 当前进行中大计划：Play-02 登录鉴权与 Redis
### 已完成
- `spring-boot-starter-data-redis`、`RedisConfig`、`RedisUtil` 已上线，基础缓存能力可用。
- Sa-Token 登录/登出链路、`AuthController`、`SysUserServiceImpl.login/logout/getCurrentUser` 已打通。
- Knife4j 文档（`/doc.html`）可访问，所有 DTO 已加 `jakarta.validation` 注解并在控制器中启用 `@Valid`。
- 用户/角色/菜单 CRUD 与分配接口配合 Sa-Token 可在登录后正常使用。

### 待完成
- 按 Play-02 规划，将 Sa-Token Token 存储切换至 Redis（需要引入 `sa-token-dao-redis` 并配置）。
- 用户分页接口缺少 `UserQueryDTO` 和条件筛选，需补充模糊查询/状态过滤并限制最大页尺寸。
- 登录接口缺少防暴力破解（频率限制、验证码或滑块等），需要结合 Redis/令牌桶落地。
- 审查 `SaTokenConfig` 白名单路径，补充接口级权限校验（RBAC）以防越权，为进入 Play-03 做准备。
- 安全配置完成后补上最小验证用例（登录成功/失败、分页过滤等）。

## 风险与待办（跨计划）
### 高优先级
1. **接口越权风险**：目前仅校验是否登录，尚未做基于角色/权限的接口授权，需要在 Play-02 收尾期补齐。
2. **敏感字段暴露**：`SysUser` 实体包含密码且可能被误返回/记录，需在数据输出链路再次核查并脱敏。
3. **配置明文凭据**：`application.yaml` 中的数据库密码已入库，需改为环境变量或 `application-local.yaml` + `.gitignore` 管理。
4. **登录防护缺失**：无节流/验证码，容易被暴力破解，需使用 Redis 计数 + 锁定策略。
5. **Sa-Token 白名单/拦截路径**：需要复核 `/doc.html`、`/v3/api-docs` 等白名单是否应对外暴露，并在后续增加基于权限的 `StpUtil.checkPermission`。

### 中/低优先级
1. **全局异常信息泄露**：`GlobalExceptionHandler` 仍 `printStackTrace`，需统一日志并避免堆栈回传。
2. **缺少操作日志**：对关键增删改操作尚未记录审计日志。
3. **分页防护**：所有分页接口需要最大页大小限制，防止大查询拖垮数据库。
4. **级联清理策略**：删除用户/角色/菜单时暂未清理关联缓存及二级表，需在实现安全策略后补齐。
