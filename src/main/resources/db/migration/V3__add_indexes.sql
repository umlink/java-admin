-- 为关联表和常用查询字段添加索引
-- 提升用户角色、角色菜单查询性能

-- sys_user_role 索引：user_id 和 role_id 单独索引，覆盖更多查询场景
CREATE INDEX idx_user_role_user_id ON sys_user_role (user_id);
CREATE INDEX idx_user_role_role_id ON sys_user_role (role_id);

-- sys_role_menu 索引：role_id 和 menu_id 单独索引
CREATE INDEX idx_role_menu_role_id ON sys_role_menu (role_id);
CREATE INDEX idx_role_menu_menu_id ON sys_role_menu (menu_id);

-- sys_menu 索引：parent_id 用于树形查询
CREATE INDEX idx_menu_parent_id ON sys_menu (parent_id);
