# 项目上下文快照

## 大计划进度
- Play-01 权限后台基础：✅ 完成
- Play-02 登录鉴权与 Redis：✅ 完成
- Play-03 权限分配与缓存设计：✅ 完成
- Play-04 日志与异常：✅ 完成
- Play-05 订单系统核心流程：✅ 完成
  - 第 11 天：创建订单核心流程 ✅
  - 第 12 天：订单查询与取消 ✅
  - 第 13 天：Redis 缓存与幂等 ✅
- Play-XX 前端同步：⏳ 待执行

## Play-05 已完成功能清单

### 1. 创建订单核心流程
- `OrderStatusEnum` 订单状态枚举（待支付/已支付/已发货/已完成/已取消）
- `CreateOrderDTO` / `OrderItemDTO` / `CreateOrderVO` 数据传输对象
- `/order/create` 接口，支持多商品下单
- 商品校验（存在性 + 上架状态）
- 库存乐观锁扣减（`version` 字段）
- 库存扣减失败自动回滚机制
- 订单号生成（`yyyyMMddHHmmss` + 6位随机数）
- `@Transactional` 事务控制

### 2. 订单查询与取消
- `/order/page` 分页查询订单（仅查自己的订单）
- `/order/{id}` 查询订单详情（含明细）
- `/order/cancel` 取消订单（仅待支付状态可取消）
- 取消订单时自动释放库存
- 订单状态流转校验

### 3. Redis 缓存与幂等
- **商品缓存**：`product:info:{productId}`，TTL 5min
- **幂等设计**：`order:idempotent:{idempotentKey}`，TTL 1h
  - 前端生成 UUID 作为幂等键
  - 重复请求直接返回上次结果
- **库存乐观锁**：SQL 层校验 `available_stock >= quantity AND version = #{version}`

## 遗留事项
- JWT 密钥硬编码在 application.yaml，生产前需改为环境变量

## 下一步选项
1. 执行前端同步（`sync-client`）
2. 继续 Play-06
3. 其他需求
