INSERT INTO sys_user (username, password, nickname, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 1);

INSERT INTO sys_role (role_code, role_name, status)
VALUES ('ADMIN', '超级管理员', 1);

INSERT INTO sys_menu (parent_id, menu_name, menu_type, permission_code, sort_num, status)
VALUES
    (0, '系统管理', 1, NULL, 1, 1),
    (1, '用户管理', 2, 'sys:user:list', 1, 1),
    (1, '角色管理', 2, 'sys:role:list', 2, 1),
    (1, '菜单管理', 2, 'sys:menu:list', 3, 1);

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1);

INSERT INTO sys_role_menu (role_id, menu_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4);