# Java 后端转岗：第 11～13 天执行手册（订单系统 - 核心流程）

## 🎯 总目标（3 天）

在 3 天内完成：

- 创建订单接口（核心）
- 查询订单列表、详情
- 取消订单接口
- 订单状态流转
- Redis 缓存商品信息
- 幂等设计

---

## 📦 一、项目结构新增

```text
src/main/java/com/example/admin
├── dto
│   ├── CreateOrderDTO.java (新增)
│   ├── OrderItemDTO.java (新增)
│   ├── OrderQueryDTO.java (新增)
│   └── CancelOrderDTO.java (新增)
├── vo
│   ├── OrderVO.java (新增)
│   ├── OrderDetailVO.java (新增)
│   └── CreateOrderVO.java (新增)
├── enums
│   └── OrderStatusEnum.java (新增)
└── service
    └── OrderDomainService.java (新增，领域服务)
```

---

## 二、第 11 天目标：创建订单核心流程

### 核心动作

1. **订单状态枚举**
   - 创建 `OrderStatusEnum`：待支付、已支付、已发货、已完成、已取消
   - 状态码 + 描述

2. **创建订单 DTO/VO**
   - 创建 `OrderItemDTO`（商品ID、数量）
   - 创建 `CreateOrderDTO`（商品列表、收货地址）
   - 创建 `CreateOrderVO`（订单ID、订单号、总金额）

3. **创建订单核心逻辑**
   - 在 `OrderService` 新增 `create()` 方法
   - 步骤：
     1. 校验商品是否存在且上架
     2. 校验库存是否充足
     3. 预扣库存（用乐观锁）
     4. 计算总金额
     5. 生成订单号（规则：yyyyMMddHHmmss + 6位随机数）
     6. 创建订单记录
     7. 创建订单明细记录
   - 加 `@Transactional` 保证事务
   - 创建失败时库存自动回滚

4. **创建订单接口**
   - 新增 `/api/order/create` 接口
   - 需要登录鉴权

### 验收标准

- 创建订单成功
- 库存正确扣减
- 订单和订单明细数据正确
- 库存不足时创建失败

---

## 三、第 12 天目标：订单查询与取消

### 核心动作

1. **订单查询**
   - 创建 `OrderQueryDTO`（订单号、状态、时间范围、分页）
   - 创建 `OrderVO`（订单基础信息）
   - 创建 `OrderDetailVO`（订单 + 明细列表）
   - 在 `OrderService` 新增 `page()` 方法（分页列表）
   - 在 `OrderService` 新增 `detail()` 方法（详情）
   - 只能查询自己的订单
   - 新增 `/api/order/page` 接口
   - 新增 `/api/order/{id}` 接口

2. **取消订单**
   - 创建 `CancelOrderDTO`（订单ID、取消原因）
   - 在 `OrderService` 新增 `cancel()` 方法
   - 只有待支付状态可以取消
   - 步骤：
     1. 校验订单状态
     2. 更新订单状态为已取消
     3. 释放预扣库存
   - 加 `@Transactional`
   - 新增 `/api/order/cancel` 接口

3. **订单状态流转校验**
   - 待支付 → 已支付 / 已取消
   - 已支付 → 已发货
   - 已发货 → 已完成
   - 非法状态变更抛出异常

### 验收标准

- 订单分页查询正常
- 订单详情包含明细
- 取消订单成功且库存释放
- 非待支付订单取消失败

---

## 四、第 13 天目标：Redis 缓存与幂等

### 核心动作

1. **Redis 缓存商品信息**
   - 缓存 Key：`product:info:{productId}`
   - 创建订单时先从缓存查商品
   - 缓存不存在查数据库再回写缓存
   - 商品更新时删除缓存

2. **Redis 预占库存（可选优化）**
   - 用 Redis List 做预占队列
   - 创建订单先入队，支付成功后确认

3. **幂等设计 - 创建订单**
   - 方案：前端生成唯一幂等号（uuid）
   - 后端用 Redis 记录：`order:idempotent:{idempotentKey}`
   - 相同幂等号直接返回上次结果
   - 在 `CreateOrderDTO` 加 `idempotentKey` 字段

4. **压测简单验证**
   - 用 jmeter 或简单循环并发创建订单
   - 验证不会超卖

### 验收标准

- 商品缓存生效
- 相同幂等号重复创建返回同一订单
- 并发下单不会超卖（乐观锁生效）

---

## 五、重点学习点

1. **订单号生成**
   - 时间戳 + 随机数
   - 保证全局唯一

2. **状态机设计**
   - 订单状态流转
   - 非法状态拦截

3. **幂等设计**
   - 幂等号方案
   - Redis 去重

4. **乐观锁**
   - `update stock set available = available - ? where id = ? and available >= ?`
   - 返回 0 表示库存不足

---

## 六、面试点整理

1. 订单号怎么生成？
2. 如何保证接口幂等？
3. 如何防止超卖？乐观锁 vs 悲观锁？
4. 状态机设计思路？
5. 缓存更新策略？