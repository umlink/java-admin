package com.example.admin.common.constant;

/**
 * 系统权限码常量，与 sys_menu.permission_code 保持一致
 */
public final class PermissionConstants {

    private PermissionConstants() {}

    /** 超管角色编码，拥有所有权限，与 StpInterfaceImpl.SUPER_ADMIN_ROLE 保持一致 */
    public static final String SUPER_ADMIN_ROLE = "ADMIN";

    /** 通配符权限码，表示拥有所有权限 */
    public static final String ALL_PERMISSIONS = "*";

    // 用户管理
    public static final String USER_LIST   = "sys:user:list";
    public static final String USER_CREATE = "sys:user:create";
    public static final String USER_UPDATE = "sys:user:update";
    public static final String USER_DELETE = "sys:user:delete";
    public static final String USER_ASSIGN_ROLE = "sys:user:assign-role";

    // 角色管理
    public static final String ROLE_LIST   = "sys:role:list";
    public static final String ROLE_CREATE = "sys:role:create";
    public static final String ROLE_UPDATE = "sys:role:update";
    public static final String ROLE_DELETE = "sys:role:delete";
    public static final String ROLE_ASSIGN_MENU = "sys:role:assign-menu";

    // 菜单管理
    public static final String MENU_LIST   = "sys:menu:list";
    public static final String MENU_CREATE = "sys:menu:create";
    public static final String MENU_UPDATE = "sys:menu:update";
    public static final String MENU_DELETE = "sys:menu:delete";

    // 操作日志管理
    public static final String OPERATE_LOG_LIST = "sys:operate-log:list";
    public static final String OPERATE_LOG_DETAIL = "sys:operate-log:detail";

    // 订单管理
    public static final String ORDER_CREATE = "order:create";
    public static final String ORDER_LIST = "order:list";
    public static final String ORDER_DETAIL = "order:detail";
    public static final String ORDER_CANCEL = "order:cancel";
}
