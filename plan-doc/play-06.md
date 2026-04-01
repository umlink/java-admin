# Java 后端转岗：第 14～16 天执行手册（订单系统 - MQ 异步）

## 🎯 总目标（3 天）

在 3 天内完成：

- RabbitMQ 接入
- 支付回调模拟 + MQ 异步通知
- 延迟关闭订单（死信队列）
- 订单完成后短信/邮件通知（异步）
- MQ 消息可靠性保证

---

## 📦 一、项目结构新增

```text
src/main/java/com/example/admin
├── config
│   ├── RabbitMQConfig.java (新增)
│   └── RabbitMQDelayConfig.java (新增)
├── mq
│   ├── producer
│   │   ├── OrderMessageProducer.java (新增)
│   │   └── NotifyMessageProducer.java (新增)
│   └── consumer
│       ├── OrderMessageConsumer.java (新增)
│       ├── OrderDelayConsumer.java (新增)
│       └── NotifyMessageConsumer.java (新增)
├── dto
│   ├── PayCallbackDTO.java (新增)
│   └── OrderMessageDTO.java (新增)
└── listener
    └── OrderPaySuccessListener.java (新增)
```

---

## 二、第 14 天目标：RabbitMQ 接入与支付回调

### 核心动作

1. **RabbitMQ 接入**
   - pom.xml 引入 `spring-boot-starter-amqp`
   - 配置 `application.yaml` RabbitMQ 连接
   - 创建 `RabbitMQConfig` 配置类
   - 创建交换机、队列、绑定
     - 直连交换机：`order.direct`
     - 队列：`order.pay.success`
     - 绑定 Key：`order.pay.success`

2. **支付回调模拟**
   - 创建 `PayCallbackDTO`（订单号、支付状态、支付时间）
   - 新增 `/api/pay/callback` 接口（模拟第三方支付回调）
   - 回调逻辑：
     1. 验签（模拟）
     2. 查询订单是否存在
     3. 判断订单状态是否为待支付
     4. 更新订单状态为已支付
     5. 确认扣库存
     6. 发送 MQ 消息：订单支付成功

3. **订单支付成功消息生产**
   - 创建 `OrderMessageDTO`（订单ID、订单号、用户ID）
   - 创建 `OrderMessageProducer`
   - 发送消息到 `order.pay.success` 队列

4. **订单支付成功消息消费**
   - 创建 `OrderMessageConsumer`
   - 监听 `order.pay.success` 队列
   - 消费逻辑：记录日志 + 后续扩展

### 验收标准

- RabbitMQ 连接成功
- 支付回调接口正常更新订单状态
- MQ 消息发送和消费成功

---

## 三、第 15 天目标：延迟关闭订单

### 核心动作

1. **延迟队列配置（死信队列方案）**
   - 创建 `RabbitMQDelayConfig`
   - 创建延迟交换机 + 延迟队列（消息 TTL 30分钟）
   - 延迟队列绑定死信交换机
   - 死信交换机 + 死信队列
   - 实际消费监听死信队列

2. **创建订单时发送延迟消息**
   - 创建订单成功后，发送延迟消息到延迟队列
   - 消息内容：订单ID
   - 消息 TTL：30分钟

3. **延迟消息消费 - 关闭订单**
   - 创建 `OrderDelayConsumer`
   - 监听死信队列
   - 消费逻辑：
     1. 查询订单
     2. 如果还是待支付状态，自动取消订单
     3. 释放库存

4. **手动测试延迟**
   - 把 TTL 改成 10 秒测试
   - 验证订单自动取消

### 验收标准

- 延迟消息发送成功
- TTL 后消息进入死信队列
- 待支付订单自动取消

---

## 四、第 16 天目标：异步通知与消息可靠性

### 核心动作

1. **支付成功异步通知**
   - 创建 `NotifyMessageProducer`
   - 支付成功后发送通知消息
   - 创建 `NotifyMessageConsumer`
   - 消费逻辑：模拟发短信、发邮件（只是 log 或 sleep）

2. **消息可靠性保证**
   - 开启生产者确认（publisher-confirm-type: correlated）
   - 开启生产者返回（publisher-returns: true）
   - 开启消费者手动确认（acknowledge-mode: manual）
   - 消费者处理成功 `channel.basicAck()`
   - 消费者处理失败 `channel.basicNack()`，是否重新入队

3. **幂等消费**
   - 用 Redis 记录消费过的消息 ID：`mq:consumed:{messageId}`
   - 消费前判断是否已消费
   - 已消费直接 ack

4. **MQ 学习笔记**
   - 交换机类型：direct、topic、fanout、headers
   - 死信队列使用场景
   - 延迟队列实现方案
   - 消息可靠性如何保证

### 验收标准

- 异步通知正常执行
- 消费者手动确认生效
- 重复消息不会重复消费

---

## 五、重点学习点

1. **RabbitMQ 核心概念**
   - Exchange、Queue、Binding
   - 消息流转过程

2. **死信队列**
   - 死信触发条件
   - 延迟队列实现

3. **消息可靠性**
   - 生产者确认
   - 消费者手动 ack
   - 幂等消费

4. **异步解耦**
   - 支付回调后异步发通知
   - 主流程不受影响

---

## 六、面试点整理

1. RabbitMQ 如何保证消息不丢失？
2. 如何实现延迟队列？
3. 死信队列是什么？
4. 消息如何保证幂等？
5. 为什么用 MQ？什么场景用？