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

---

## 项目代码质量 Review 发现的问题

### 🔴 P0 - 立即修复 ✅ 已完成

| 问题 | 位置 | 状态 |
|------|------|------|
| **幂等键清理 NPE Bug** | `OrderServiceImpl.create()` catch 块 | ✅ 已修复 |
| **库存回滚时幂等键不清理** | `OrderServiceImpl.create()` | ✅ 已修复 |
| **@RepeatSubmit 与幂等键冲突** | `OrderController.create()` | ✅ 已修复 |

### 🟡 P1 - 尽快优化 ✅ 已完成

| 问题 | 位置 | 状态 |
|------|------|------|
| **库存释放无乐观锁** | `ProductStockMapper.releaseStock()` | ✅ 已修复 |
| **操作日志敏感信息脱敏** | `OperateLogAspect` | ✅ 已修复 |

### 🟢 P2 - 长期改进 ✅ 已完成

| 问题 | 位置 | 状态 |
|------|------|------|
| **部分魔法数字提取为常量** | `OperateLogAspect.MAX_LENGTH = 4000` | ✅ 已修复 |

以下 P2 问题暂不修复（改动较大，超出当前范围）：
- 缺少业务域层
- DTO/VO 转换逻辑散落（使用 MapStruct）

---

## 已修复内容总结

### P0 修复
1. **幂等键清理 NPE Bug**: 修复了 `redisUtil.get(idempotentKey)` 可能返回 null 导致的 NPE
2. **库存回滚时幂等键清理**: 库存扣减失败回滚后，清理幂等键允许用户重试
3. **@RepeatSubmit 与幂等键冲突**: 移除了创建订单接口上的 `@RepeatSubmit` 注解

### P1 修复
1. **库存释放增加乐观锁**: `releaseStock() 方法增加 version 校验和重试机制
2. **操作日志脱敏**: `@OperateLog` 注解增加 `sensitiveFields` 属性，`OperateLogAspect` 实现敏感字段脱敏（默认 password, token, secret, key）

### P2 修复
1. **魔法数字提取**: `OperateLogAspect.MAX_LENGTH` 提取为 `EntityConstants.OPERATE_LOG_MAX_LENGTH`

---

## 项目总体评分

| 维度 | 评分 | 说明 |
|------|------|------|
| 架构设计 | 7.5/10 | 分层清晰，缺少 Domain 层 |
| 代码实现 | 8.5/10 | 规范，P0/P1 问题已修复 |
| 逻辑严谨性 | 8/10 | 幂等/乐观锁完善 |
| 可复用性 | 8/10 | 注解/AOP 设计好 |
| 安全性 | 7/10 | 操作日志已脱敏 |
| 技术选型 | 8.5/10 | 选得很好 |
| 代码规范 | 8.5/10 | 整体规范 |
| **总体** | **8.1/10 | **良好的生产级项目** |

---

## 遗留事项
- JWT 密钥硬编码在 application.yaml，生产前需改为环境变量
- 数据库密码硬编码，需改为环境变量

## 下一步选项
1. 执行前端同步（`sync-client`）
2. 继续 Play-06
3. 其他需求
