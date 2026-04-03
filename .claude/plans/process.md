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

### 🔴 P0 - 立即修复

| 问题 | 位置 | 影响 | 说明 |
|------|------|------|------|
| **幂等键清理 NPE Bug** | `OrderServiceImpl.create()` catch 块 | 高 | `redisUtil.get(idempotentKey)` 可能返回 null，导致 NPE |
| **配置信息泄露** | `application.yaml` | 严重 | 数据库密码、JWT secret 硬编码在配置文件中 |
| **库存回滚时幂等键不清理** | `OrderServiceImpl.create()` | 高 | 库存扣减失败回滚后，幂等键仍为 PROCESSING，用户无法重试 |
| **@RepeatSubmit 与幂等键冲突** | `OrderController.create()` | 中 | 创建订单同时有 @RepeatSubmit 和幂等键，@RepeatSubmit 会先拦截，导致幂等键机制失效 |

### 🟡 P1 - 尽快优化

| 问题 | 位置 | 影响 | 说明 |
|------|------|------|------|
| **库存释放无乐观锁** | `ProductStockMapper.releaseStock()` | 中 | 释放库存未检查 version，高并发可能出现数据不一致 |
| **缺少接口限流** | 全局 | 中 | 除登录接口外，其他接口无限流保护 |
| **操作日志可能记录敏感信息** | `OperateLogAspect` | 中 | 完整记录请求/响应，可能包含密码、token |

### 🟢 P2 - 长期改进

| 问题 | 影响 | 说明 |
|------|------|------|
| **缺少单元测试** | 低 | 项目中无任何单元测试和集成测试 |
| **缺少业务域层** | 低 | 订单创建逻辑全部在 OrderServiceImpl，建议拆分为 OrderDomainService |
| **DTO/VO 转换逻辑散落** | 低 | 建议使用 MapStruct 或抽离 Converter 层 |
| **缺少监控** | 低 | 建议集成 Micrometer + Prometheus + Actuator |
| **部分魔法数字** | 低 | `OperateLogAspect.MAX_LENGTH = 4000` 等建议提取为常量 |

---

## 项目总体评分

| 维度 | 评分 | 说明 |
|------|------|------|
| 架构设计 | 7.5/10 | 分层清晰，缺少 Domain 层 |
| 代码实现 | 8/10 | 规范，但有几处 Bug |
| 逻辑严谨性 | 7/10 | 幂等/乐观锁好，细节需优化 |
| 可复用性 | 8/10 | 注解/AOP 设计好 |
| 安全性 | 6/10 | 配置泄露是硬伤 |
| 技术选型 | 8.5/10 | 选得很好 |
| 代码规范 | 8/10 | 整体规范 |
| **总体** | **7.6/10 | **良好的生产级项目基础** |

---

## 遗留事项
- JWT 密钥硬编码在 application.yaml，生产前需改为环境变量
- 数据库密码硬编码，需改为环境变量

## 下一步选项
1. 修复 P0 问题
2. 执行前端同步（`sync-client`）
3. 继续 Play-06
4. 其他需求
