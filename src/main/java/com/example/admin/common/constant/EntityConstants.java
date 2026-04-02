package com.example.admin.common.constant;

/**
 * 实体状态常量
 */
public final class EntityConstants {

    private EntityConstants() {}

    /** 状态：启用 */
    public static final int STATUS_ENABLED = 1;

    /** 状态：禁用 */
    public static final int STATUS_DISABLED = 0;

    /** 逻辑删除：未删除 */
    public static final int DELETED_NO = 0;

    /** 逻辑删除：已删除 */
    public static final int DELETED_YES = 1;
}
