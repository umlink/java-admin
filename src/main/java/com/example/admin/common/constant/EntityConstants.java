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

    /** 分页最大页大小 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 商品单次购买最大数量 */
    public static final int MAX_PURCHASE_QUANTITY = 999;

    /** 库存扣减最大重试次数 */
    public static final int MAX_DEDUCT_RETRY_TIMES = 3;

    /** 库存扣减重试间隔（毫秒） */
    public static final long DEDUCT_RETRY_INTERVAL_MS = 10;
}
