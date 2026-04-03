package com.example.admin.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 待支付
     */
    PENDING_PAYMENT(1, "待支付"),

    /**
     * 已支付
     */
    PAID(2, "已支付"),

    /**
     * 已发货
     */
    SHIPPED(3, "已发货"),

    /**
     * 已完成
     */
    COMPLETED(4, "已完成"),

    /**
     * 已取消
     */
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 code 获取枚举
     */
    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 校验状态流转是否合法
     *
     * @param from 源状态
     * @param to   目标状态
     * @return 是否合法
     */
    public static boolean isValidTransition(OrderStatusEnum from, OrderStatusEnum to) {
        if (from == null) {
            return false;
        }
        return switch (from) {
            case PENDING_PAYMENT -> to == PAID || to == CANCELLED;
            case PAID -> to == SHIPPED;
            case SHIPPED -> to == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

}
