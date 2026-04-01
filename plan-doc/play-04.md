# Java 后端转岗：第 8～10 天执行手册（订单系统 - 商品库存）

## 🎯 总目标（3 天）

在 3 天内完成：

- 订单系统项目准备（可以新建模块，也可以继续在当前项目）
- 商品模块 CRUD
- 库存模块 CRUD
- 商品库存联查
- 数据库表设计（商品、库存、订单、订单明细）
- Spring 事务基础体验

---

## 📦 一、项目结构（订单系统）

```text
src/main/java/com/example/admin
├── entity
│   ├── Product.java (新增)
│   ├── Stock.java (新增)
│   ├── Order.java (新增)
│   └── OrderItem.java (新增)
├── mapper
│   ├── ProductMapper.java (新增)
│   ├── StockMapper.java (新增)
│   ├── OrderMapper.java (新增)
│   └── OrderItemMapper.java (新增)
├── service
│   ├── ProductService.java (新增)
│   ├── StockService.java (新增)
│   ├── OrderService.java (新增)
│   └── OrderItemService.java (新增)
├── service/impl
│   ├── ProductServiceImpl.java (新增)
│   ├── StockServiceImpl.java (新增)
│   ├── OrderServiceImpl.java (新增)
│   └── OrderItemServiceImpl.java (新增)
├── controller
│   ├── ProductController.java (新增)
│   ├── StockController.java (新增)
│   └── OrderController.java (新增)
├── dto
│   ├── ProductCreateDTO.java (新增)
│   ├── ProductUpdateDTO.java (新增)
│   ├── ProductQueryDTO.java (新增)
│   ├── StockAddDTO.java (新增)
│   └── StockDeductDTO.java (新增)
└── vo
    ├── ProductVO.java (新增)
    ├── StockVO.java (新增)
    └── ProductDetailVO.java (新增)
```

---

## 二、第 8 天目标：订单系统表设计与商品模块

### 核心动作

1. **数据库表设计**
   - `product` 商品表（id、商品名称、描述、价格、状态、图片、创建时间）
   - `stock` 库存表（id、商品ID、总库存、可用库存、锁定库存、创建时间）
   - `order` 订单表（id、订单号、用户ID、总金额、状态、支付时间、创建时间）
   - `order_item` 订单明细表（id、订单ID、商品ID、商品名称、单价、数量、小计）

2. **商品模块 CRUD**
   - 创建 `Product` Entity
   - 创建 `ProductMapper`
   - 创建 `ProductService` + `ProductServiceImpl`
   - 创建 `ProductController`
   - 商品新增、修改、删除、详情、分页列表
   - 商品上下架

### 验收标准

- 4 张表创建成功
- 商品 CRUD 接口正常

---

## 三、第 9 天目标：库存模块

### 核心动作

1. **库存模块 CRUD**
   - 创建 `Stock` Entity
   - 创建 `StockMapper`
   - 创建 `StockService` + `StockServiceImpl`
   - 创建 `StockController`
   - 库存查询、库存初始化

2. **库存操作**
   - 加库存（采购入库）
   - 扣库存（下单预扣）
   - 释放库存（取消订单）
   - 确认扣库存（支付成功）
   - 库存不能为负数的校验

3. **商品库存联查**
   - 创建 `ProductDetailVO`（商品信息 + 库存信息）
   - 商品详情接口返回库存信息

### 验收标准

- 库存加扣正常
- 库存负数校验生效
- 商品详情包含库存信息

---

## 四、第 10 天目标：Spring 事务体验

### 核心动作

1. **Spring 事务配置**
   - 确认 `@Transactional` 生效
   - 事务传播属性基础理解

2. **商品 + 库存原子操作**
   - 新增商品时同时初始化库存
   - 用 `@Transactional` 保证原子性
   - 模拟异常场景测试回滚

3. **库存扣减事务**
   - 批量扣减多个商品库存
   - 任何一个失败全部回滚

4. **事务学习笔记**
   - 整理 `@Transactional` 常用参数
   - 整理事务传播行为（REQUIRED、REQUIRES_NEW）
   - 整理事务失效场景

### 验收标准

- 商品 + 库存原子操作成功
- 异常时事务回滚生效
- 能讲清楚 3 种事务传播行为

---

## 五、重点学习点

1. **订单相关表设计**
   - 商品与库存一对一
   - 订单与订单明细一对多
   - 订单状态流转设计

2. **库存扣减方式**
   - 预扣库存 → 确认扣减 / 释放
   - SQL 乐观锁扣库存：`update stock set available = available - ? where id = ? and available >= ?`

3. **Spring 事务**
   - `@Transactional` 使用
   - 事务回滚机制
   - 事务传播行为

---

## 六、面试点整理

1. 订单系统表如何设计？
2. 如何防止超卖？
3. Spring 事务原理？
4. 事务传播行为有哪些？
5. 事务失效的场景？