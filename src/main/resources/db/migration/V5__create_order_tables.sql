-- 商品表
CREATE TABLE IF NOT EXISTS product
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price       DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    status      TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-上架，0-下架',
    deleted     TINYINT NOT NULL DEFAULT 0,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by  BIGINT DEFAULT NULL,
    updated_by  BIGINT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品库存表（独立出来避免锁表影响商品信息查询）
CREATE TABLE IF NOT EXISTS product_stock
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id    BIGINT NOT NULL COMMENT '商品ID',
    total_stock   INT NOT NULL DEFAULT 0 COMMENT '总库存',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '可用库存',
    locked_stock  INT NOT NULL DEFAULT 0 COMMENT '锁定库存',
    version       INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no       VARCHAR(32) NOT NULL COMMENT '订单号',
    user_id        BIGINT NOT NULL COMMENT '用户ID',
    total_amount   DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    status         TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消',
    cancel_reason  VARCHAR(256) DEFAULT NULL COMMENT '取消原因',
    address        VARCHAR(512) NOT NULL COMMENT '收货地址',
    deleted        TINYINT NOT NULL DEFAULT 0,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by     BIGINT DEFAULT NULL,
    updated_by     BIGINT DEFAULT NULL,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_item
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id   BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称（快照）',
    price      DECIMAL(10, 2) NOT NULL COMMENT '商品单价（快照）',
    quantity   INT NOT NULL COMMENT '购买数量',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '小计',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
