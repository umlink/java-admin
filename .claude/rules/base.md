# 基础约束

- 项目需求计划：`plan-doc/` 目录，`plan-tree.md` 为工作大纲，`play-{n}.md` 为各阶段计划
- 编码前不能新增需求外的代码
- 稍微复杂的逻辑需要自动添加注释
- 代码逻辑需要考虑各种边界情况
- SQL 文件：`src/main/resources/sql/`（参考用），迁移脚本：`src/main/resources/db/migration/`
- 每完成一次任务更新 `plan-doc/process.md`
- 开始编码工作前必须先列出工作记录，必须等我确认后才能继续
- 每次完成一次任务后，review and check 最新完成的代码的质量是否有潜在问题和值得优化的地方

# 自定义输入

- 当我输入 `check-all` 时，你需要作为java开发专家，在当前项目下从架构设计、代码实现、逻辑严谨性、可复用性、安全性、技术选型、代码规范等方面分析项目计
  划和实现情况，实现的状况，明确哪里需要优化的，哪里存在潜在问题，哪里存在明显问题。

# 技术决策备忘

- ID 生成：AUTO_INCREMENT
- 权限缓存：Redis TTL 5min + 主动失效（角色/菜单变更时清除）
- 超管：role_code=ADMIN → getPermissionList 返回 ["*"]，跳过所有 @SaCheckPermission
- 分页：统一 pageNum/pageSize，查询参数 > 1 个用 POST + QueryDTO
- 数据库迁移：Flyway，V1__init_schema.sql ~ V6__init_product_data.sql
- 审计字段：createdAt/updatedAt 由 MySQL 自动维护，createdBy/updatedBy 由 AuditMetaObjectHandler 填充
- 防重复提交：@RepeatSubmit + Redis Key = repeat:{userId}:{uri}:{md5(params)}，TTL 5s
- 操作日志：@OperateLog + AOP 切面，记录操作人、请求参数、响应结果、执行时长
- 链路追踪：TraceIdFilter 生成 TraceId，MDC 传递，日志输出包含 TraceId
- 日志框架：Logback + 异步输出 + 按日期滚动（保留30天）
- 订单号生成：yyyyMMddHHmmss + 6位随机数
- 库存扣减：乐观锁（version 字段）
- 订单幂等：Redis Key = order:idempotent:{idempotentKey}，TTL 1h

# 前端同步完成
- 技术栈：Vue 3 + TypeScript + Vite + Pinia + TailwindCSS + daisyUI + VueUse
- 已完成模块：登录、仪表盘、用户管理、角色管理、菜单管理、操作日志
- 目录结构：types/、api/、composables/、stores/、views/、layouts/、utils/
- 路由守卫：基于权限码的访问控制

# Redis Key 规范
- `perm:user:{userId}`：用户权限码列表，TTL 5min
- `role:user:{userId}`：用户角色码列表，TTL 5min
- `login:fail:{username}`：登录失败次数，TTL 30min
- `login:lock:{username}`：登录锁定标记，TTL 30min
- `repeat:{userId}:{uri}:{md5(params)}`：防重复提交标记，TTL 5s
- `product:info:{productId}`：商品信息缓存，TTL 5min
- `order:idempotent:{idempotentKey}`：创建订单幂等标记，TTL 1h

# 编码规则

- 添加依赖前检测漏洞，只使用安全版本
- 编码严格考虑安全问题和内存泄露风险
- 使用 `@RequiredArgsConstructor + final` 字段注入
- DTO 中使用 `requiredMode = Schema.RequiredMode.REQUIRED` 替代 `required = true`
- 审计字段规范：`createdAt/updatedAt` 由 MySQL 维护；`createdBy/updatedBy` 由 `AuditMetaObjectHandler` 填充，Service 层不赋值
- 分页参数：统一 `pageNum`（默认1）/ `pageSize`（默认10）
- 分页接口：查询参数 > 1 个用 POST + QueryDTO，仅分页参数可用 GET
- 每次执行变更任务后列出变更项条目

# 进度更新规范（plan-doc/process.md）

- 记录：大计划进度 + 当前进行中计划的未完成项 + 遗留问题
- 已完成内容自动清理，不重复记录
- 新对话首句：「读取 plan-doc/process.md，继续 Play-XX」
