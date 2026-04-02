INSERT INTO sys_user (username, password, nickname, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 1);

INSERT INTO sys_role (role_code, role_name, status)
VALUES ('ADMIN', '超级管理员', 1);

INSERT INTO sys_menu (parent_id, menu_name, menu_type, permission_code, sort_num, status)
VALUES
    (0, '系统管理', 1, NULL, 1, 1),
    (1, '用户管理', 2, 'sys:user:list', 1, 1),
    (1, '角色管理', 2, 'sys:role:list', 2, 1),
    (1, '菜单管理', 2, 'sys:menu:list', 3, 1),
    -- 用户管理按钮
    (2, '新增用户', 3, 'sys:user:create', 1, 1),
    (2, '修改用户', 3, 'sys:user:update', 2, 1),
    (2, '删除用户', 3, 'sys:user:delete', 3, 1),
    (2, '分配角色', 3, 'sys:user:assign-role', 4, 1),
    -- 角色管理按钮
    (3, '新增角色', 3, 'sys:role:create', 1, 1),
    (3, '修改角色', 3, 'sys:role:update', 2, 1),
    (3, '删除角色', 3, 'sys:role:delete', 3, 1),
    (3, '分配菜单', 3, 'sys:role:assign-menu', 4, 1),
    -- 菜单管理按钮
    (4, '新增菜单', 3, 'sys:menu:create', 1, 1),
    (4, '修改菜单', 3, 'sys:menu:update', 2, 1),
    (4, '删除菜单', 3, 'sys:menu:delete', 3, 1);

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1);

INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4),
       (1, 5), (1, 6), (1, 7), (1, 8),
       (1, 9), (1, 10), (1, 11), (1, 12),
       (1, 13), (1, 14), (1, 15);