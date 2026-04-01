# Java 后端转岗：第 6～7 天执行手册（权限分配与缓存）

## 🎯 总目标（2 天）

在 2 天内完成：

- 用户分配角色
- 角色分配菜单
- 当前用户权限聚合接口
- 权限缓存设计
- 数据库索引优化
- 防重复提交

---

## 📦 一、项目结构新增

```text
src/main/java/com/example/admin
├── dto
│   ├── UserAssignRoleDTO.java (新增)
│   └── RoleAssignMenuDTO.java (新增)
├── vo
│   ├── UserRoleVO.java (新增)
│   ├── RoleMenuVO.java (新增)
│   └── PermissionVO.java (新增)
├── annotation
│   └── RepeatSubmit.java (新增)
└── interceptor
    └── RepeatSubmitInterceptor.java (新增)
```

---

## 二、第 6 天目标：用户角色与角色菜单分配

### 核心动作

1. **用户分配角色**
   - 创建 `UserAssignRoleDTO`（用户ID + 角色ID列表）
   - 在 `SysUserService` 新增 `assignRole()` 方法
   - 先删除用户旧角色，再批量插入新角色
   - 创建 `UserRoleVO`（用户信息 + 拥有的角色列表）
   - 新增 `/api/user/{id}/roles` 接口（获取用户角色）
   - 新增 `/api/user/{id}/assign-roles` 接口（分配角色）

2. **角色分配菜单**
   - 创建 `RoleAssignMenuDTO`（角色ID + 菜单ID列表）
   - 在 `SysRoleService` 新增 `assignMenu()` 方法
   - 先删除角色旧菜单，再批量插入新菜单
   - 创建 `RoleMenuVO`（角色信息 + 拥有的菜单列表）
   - 新增 `/api/role/{id}/menus` 接口（获取角色菜单）
   - 新增 `/api/role/{id}/assign-menus` 接口（分配菜单）

3. **菜单树形结构**
   - 在 `SysMenuService` 新增 `tree()` 方法
   - 递归构建菜单树
   - 新增 `/api/menu/tree` 接口

### 验收标准

- 可以给用户分配多个角色
- 可以给角色分配多个菜单
- 菜单树接口返回正确层级结构

---

## 三、第 7 天目标：权限聚合与缓存设计

### 核心动作

1. **当前用户权限聚合**
   - 创建 `PermissionVO`（用户信息 + 角色列表 + 菜单树 + 权限码集合）
   - 在 `SysUserService` 新增 `getPermissionInfo()` 方法
     - 查询当前用户拥有的角色
     - 查询这些角色拥有的菜单
     - 提取菜单权限码集合
   - 新增 `/api/auth/permission` 接口

2. **权限缓存设计**
   - 用 Redis 缓存用户权限信息
   - 缓存 Key：`user:permission:{userId}`
   - 过期时间：2 小时
   - 分配角色/菜单时清除缓存
   - 修改用户/角色/菜单时清除相关缓存

3. **数据库索引优化**
   - 给 `sys_user_role` 的 `user_id`、`role_id` 加索引
   - 给 `sys_role_menu` 的 `role_id`、`menu_id` 加索引
   - 给 `sys_user` 的 `username` 加唯一索引（已有）
   - 给 `sys_role` 的 `role_code` 加唯一索引（已有）
   - 给 `sys_menu` 的 `parent_id` 加索引

4. **防重复提交**
   - 创建 `@RepeatSubmit` 注解
   - 创建 `RepeatSubmitInterceptor` 拦截器
   - 用 Redis 实现防重（Key：`repeat:{userId}:{uri}:{md5(params)}`）
   - 过期时间：5 秒
   - 给新增、修改接口加上注解

### 验收标准

- 权限聚合接口返回完整信息
- 权限缓存生效（第二次请求更快）
- 修改权限后缓存清除
- 短时间内重复提交被拦截

---

## 四、重点学习点

1. **RBAC 权限模型**
   - 用户 → 角色 → 菜单 → 权限
   - 多对多关系处理
   - 权限聚合逻辑

2. **Redis 缓存**
   - 缓存 Key 设计
   - 缓存过期策略
   - 缓存更新时机（先删后更）

3. **数据库索引**
   - 索引优缺点
   - 哪些字段该加索引
   - 联合索引顺序

4. **防重复提交**
   - 注解 + 拦截器实现
   - Redis 做防重存储
   - 请求参数 MD5

---

## 五、面试点整理

1. RBAC 权限模型的理解？
2. 缓存更新策略有哪些？先删还是先更？
3. 数据库索引原理？B+ 树 vs B 树？
4. 如何设计防止重复提交？
5. 如何防止缓存穿透、击穿、雪崩？