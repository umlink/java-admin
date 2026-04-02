# AI 任务执行情况记录

## 更新时间
- 2026-04-02

## 大计划进度
- **Play-01 权限后台基础（第 1～3 天）**：已完成。
- **Play-02 登录鉴权与 Redis（第 4～5 天）**：实现完成，有严重问题待修复。
- **Play-03 权限分配与缓存设计**：待启动，等 Play-02 问题修复后进入。
- **Play-04 及以后**：未启动。

---

## Play-02 待修复问题

### 严重（必须修复）
- Sa-Token 拦截器路径 `/api/v1/**` 与实际 Controller 路径不匹配，登录校验完全失效
- 所有 Controller 接口缺少权限校验注解（`@SaCheckPermission` / `@SaCheckRole`），存在越权风险
- `LoginRateLimiter` incr/expire 非原子操作，存在竞态条件，可能导致永久锁定
- `RedisUtil` 多处使用 `printStackTrace`，异常被吞掉

### 一般问题
- 登录逻辑时序：先查用户再校验状态，应先校验状态
- 删除用户未清理 `sys_user_role` 关联表
- `deleteRole` 未清理 `sys_user_role`：被删角色的用户仍持有关联，Sa-Token 查权限时会产生脏数据
- 多处使用 `BeanUtils.copyProperties`，存在字段遗漏风险
- `created_by`/`updated_by` 字段未实际使用
- 硬编码魔法值（status=1, deleted=0）未提取为常量

### 额外待优化问题
- `assignRoles` / `assignMenus` 存在 N+1 查询：循环内逐个 `getById` 校验，应改为 `listByIds` 批量查询
- `buildTree` 递归逻辑有缺陷：子菜单已构建但未挂载到父节点，返回结果实际是平铺列表，不是真正的树结构
- `GlobalExceptionHandler` 未处理 Sa-Token 异常：未登录、无权限等场景会落入通用异常，返回系统异常而不是明确的鉴权错误
- `getMenuTree()` 直接返回 `List<SysMenu>`：接口层暴露 Entity，违反 DTO/VO 分层规范
- `SaTokenConfig` 职责过重：同时承担拦截器配置和权限查询，且权限查询每次打数据库、无缓存，高并发下会成为瓶颈
- `createdAt` / `updatedAt` 仍在 Service 中手动赋值：未使用 MyBatis-Plus 自动填充，审计字段无法统一治理
- `UserVO.phone` 未脱敏：手机号直接明文返回，不符合敏感信息最小暴露原则
- `assignRoles` / `assignMenus` 采用先删后插，缺少幂等与并发保护：并发请求下存在数据错乱风险
- `RedisUtil` 过度封装：方法面过大且异常处理风格不统一，后续维护成本偏高

---

## 本次完成的工程规范修复（2026-04-02）
- 所有 DTO 中 `@Schema(required = true)` 改为 `requiredMode = Schema.RequiredMode.REQUIRED`
- 全项目 `@Autowired` 字段注入改为 `@RequiredArgsConstructor` + `final` 构造器注入
- 补充完整了 `SysMenuServiceImpl` 中缺失的接口方法实现
