# 项目上下文快照

## 大计划进度
- Play-01 权限后台基础：✅ 完成
- Play-02 登录鉴权与 Redis：✅ 完成
- Play-03 权限分配与缓存设计：⏳ 待启动

## 技术决策备忘
- ID 生成：AUTO_INCREMENT
- 权限缓存：Redis TTL 5min + 主动失效（角色/菜单变更时清除）
- 超管：role_code=ADMIN → getPermissionList 返回 ["*"]，跳过所有 @SaCheckPermission
- 分页：统一 pageNum/pageSize，查询参数 > 1 个用 POST + QueryDTO
- 数据库迁移：Flyway，V1__init_schema.sql + V2__init_data.sql
- 审计字段：createdAt/updatedAt 由 MySQL 自动维护，createdBy/updatedBy 由 AuditMetaObjectHandler 填充

## Redis Key 规范
- `perm:user:{userId}`：用户权限码列表，TTL 5min
- `role:user:{userId}`：用户角色码列表，TTL 5min
- `login:fail:{username}`：登录失败次数，TTL 30min
- `login:lock:{username}`：登录锁定标记，TTL 30min

## 遗留事项
- JWT 密钥硬编码在 application.yaml，生产前需改为环境变量

## 下一步
读取 plan-doc/play-3.md，启动 Play-03
