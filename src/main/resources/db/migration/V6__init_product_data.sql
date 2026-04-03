-- 初始化商品数据
INSERT IGNORE INTO product (id, product_name, description, price, status)
VALUES
    (1, 'iPhone 15 Pro', '苹果 iPhone 15 Pro 256GB 深空黑色', 8999.00, 1),
    (2, 'MacBook Pro 14', 'MacBook Pro 14英寸 M3 Pro 18GB+512GB', 14999.00, 1),
    (3, 'AirPods Pro 2', 'AirPods Pro 第二代 USB-C 充电盒', 1899.00, 1),
    (4, 'iPad Air', 'iPad Air 10.9英寸 M2 64GB WiFi', 4799.00, 1),
    (5, 'Apple Watch Series 9', 'Apple Watch Series 9 45mm GPS 午夜色', 3199.00, 1);

-- 初始化商品库存数据
INSERT IGNORE INTO product_stock (id, product_id, total_stock, available_stock, locked_stock, version)
VALUES
    (1, 1, 100, 100, 0, 0),
    (2, 2, 50, 50, 0, 0),
    (3, 3, 200, 200, 0, 0),
    (4, 4, 80, 80, 0, 0),
    (5, 5, 150, 150, 0, 0);
