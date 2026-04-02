# 基础约束

- 项目需求计划：`plan-doc/` 目录，`plan-tree.md` 为工作大纲，`play-{n}.md` 为各阶段计划
- 编码前不能新增需求外的代码
- 稍微复杂的逻辑需要自动添加注释
- 代码逻辑需要考虑各种边界情况
- SQL 文件：`src/main/resources/sql/`（参考用），迁移脚本：`src/main/resources/db/migration/`
- 每完成一次任务更新 `plan-doc/process.md`
- 开始编码工作前必须先列出工作记录，必须等我确认后才能继续

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
