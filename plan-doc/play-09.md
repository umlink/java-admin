# Java 后端转岗：第 25～30 天执行手册（求职输出准备）

## 🎯 总目标（6 天）

在 6 天内完成：

- 项目讲稿整理
- 面试题体系整理
- 简历重写
- 自我介绍优化
- 模拟面试

---

## 一、第 25～26 天目标：项目讲稿整理

### 核心内容

1. **权限管理后台项目讲稿**
   - 项目背景：为什么做这个项目
   - 技术栈：Java 17、Spring Boot、MyBatis-Plus、MySQL、Redis、Sa-Token、RabbitMQ、Docker
   - 项目架构：分层设计（controller、service、mapper、entity、dto、vo）
   - 核心模块：
     - 用户管理：CRUD、分页、参数校验
     - 角色管理：CRUD、分配菜单
     - 菜单管理：树形结构、权限标识
     - 权限模型：RBAC、用户→角色→菜单→权限
     - 登录鉴权：Sa-Token + Redis、Token 生成与校验
     - 权限缓存：Redis 缓存用户权限、更新时清除缓存
   - 难点与解决方案：
     - 防止重复提交：注解 + 拦截器 + Redis
     - 数据库索引优化：给关联字段加索引
     - 缓存穿透/击穿/雪崩：布隆过滤器、互斥锁、永不过期
   - 项目亮点：统一异常处理、统一返回结构、参数校验、操作日志（如有）

2. **订单系统项目讲稿**
   - 项目背景：电商订单核心流程
   - 技术栈：Spring Boot、MyBatis-Plus、MySQL、Redis、RabbitMQ
   - 核心模块：
     - 商品管理：CRUD、上下架
     - 库存管理：预扣库存、确认扣减、释放库存、乐观锁防超卖
     - 订单管理：创建订单、取消订单、订单查询、状态流转
     - 支付回调：模拟第三方支付、更新订单状态、确认扣库存
     - MQ 异步：支付成功通知、延迟关闭订单
   - 难点与解决方案：
     - 防止超卖：数据库乐观锁 + Redis 预占
     - 接口幂等：幂等号 + Redis 去重
     - 订单自动取消：RabbitMQ 死信队列实现延迟队列
     - 异步解耦：支付回调后 MQ 发通知
     - 消息可靠性：生产者确认 + 消费者手动 ack + 幂等消费
   - 项目亮点：事务边界清晰、幂等设计完善、异步流程合理

3. **微服务 Demo 讲稿**
   - 项目背景：微服务基础学习
   - 技术栈：Spring Cloud Alibaba、Nacos、Gateway、OpenFeign、Sentinel
   - 核心组件：
     - Nacos 注册中心：服务注册与发现
     - Nacos 配置中心：配置集中管理、动态刷新
     - Gateway 网关：统一入口、路由转发、过滤器
     - OpenFeign：服务间声明式调用
     - Sentinel：限流、降级、热点参数限流
   - 项目亮点：完整的微服务基础组件整合

### 输出

- 权限后台项目讲稿（word / md）
- 订单系统项目讲稿（word / md）
- 微服务 Demo 讲稿（word / md）
- 对着讲稿能流畅讲 10～15 分钟

---

## 二、第 27～28 天目标：面试题体系整理

### 核心内容

1. **Java 基础**
   - 面向对象：封装、继承、多态
   - 集合：ArrayList、LinkedList、HashMap、ConcurrentHashMap
   - 异常体系
   - Lambda、Stream、Optional

2. **JVM**
   - 内存结构
   - GC 算法与垃圾收集器
   - 类加载机制
   - 四种引用

3. **并发**
   - 线程基础
   - synchronized、volatile、Lock
   - CAS、Atomic 类
   - 线程池
   - CompletableFuture
   - 并发容器：ConcurrentHashMap、CopyOnWriteArrayList
   - 并发工具：CountDownLatch、CyclicBarrier、Semaphore、ThreadLocal

4. **Spring / Spring Boot**
   - IOC、DI
   - AOP 原理
   - Spring Boot 自动配置
   - Spring 事务
   - Spring MVC 流程

5. **MyBatis / MyBatis-Plus**
   - #{} vs ${}
   - 一级缓存、二级缓存
   - 分页插件原理
   - 懒加载

6. **MySQL**
   - 索引原理：B+ 树
   - 聚簇索引 vs 非聚簇索引
   - 最左前缀原则
   - 事务 ACID
   - 隔离级别
   - MVCC
   - 锁：行锁、表锁、间隙锁、临键锁
   - SQL 优化、explain 分析

7. **Redis**
   - 数据结构
   - 持久化：RDB、AOF
   - 过期策略、内存淘汰策略
   - 缓存穿透、击穿、雪崩
   - 分布式锁
   - 集群：主从、哨兵、Cluster

8. **RabbitMQ**
   - 核心概念：Exchange、Queue、Binding
   - 交换机类型
   - 死信队列
   - 延迟队列
   - 消息可靠性保证

9. **微服务**
   - 注册中心：Nacos
   - 配置中心：Nacos
   - 网关：Gateway
   - 服务调用：OpenFeign
   - 限流熔断：Sentinel

10. **设计模式**
    - 单例模式
    - 工厂模式
    - 策略模式
    - 模板方法模式
    - 代理模式
    - 装饰器模式

### 输出

- 面试题整理文档（分模块）
- 每题都能用自己的话讲出来

---

## 三、第 29 天目标：简历重写

### 核心内容

1. **个人信息**
   - 姓名、电话、邮箱、GitHub、博客（如有）

2. **求职意向**
   - Java 后端工程师 / Java 全栈工程师

3. **专业技能**
   - Java 基础：集合、并发、JVM
   - 框架：Spring Boot、Spring Cloud、MyBatis-Plus
   - 数据库：MySQL、Redis
   - 中间件：RabbitMQ
   - 工具：Git、Docker、Maven
   - 前端：React、Vue（可以提，但别喧宾夺主）

4. **工作经历**
   - 公司名称、时间、职位
   - 用 STAR 法则描述项目
   - 突出后端相关工作
   - 量化成果

5. **项目经历（重点）**
   - 权限管理后台（独立项目）
     - 项目描述：基于 RBAC 的权限管理系统，支持用户、角色、菜单管理，登录鉴权
     - 技术栈：Java 17、Spring Boot、MyBatis-Plus、MySQL、Redis、Sa-Token
     - 责任描述：
       - 设计并实现 RBAC 权限模型，用户-角色-菜单多对多关系
       - 集成 Sa-Token + Redis 实现登录鉴权与权限缓存
       - 使用乐观锁防止超卖（订单系统）
       - 集成 RabbitMQ 实现支付回调异步通知与延迟关闭订单
       - 统一异常处理、统一返回结构、参数校验
   - 订单系统（独立项目）
     - 项目描述：电商订单核心系统，包含商品、库存、订单、支付回调、异步通知
     - 技术栈：Spring Boot、MyBatis-Plus、MySQL、Redis、RabbitMQ
     - 责任描述：
       - 设计商品、库存、订单、订单明细等表结构
       - 实现乐观锁防超卖，幂等设计保证接口幂等
       - 使用 RabbitMQ 死信队列实现延迟 30 分钟自动关闭订单
       - 支付回调后 MQ 异步发送通知，实现系统解耦
       - Redis 缓存商品信息，提升查询性能
   - 微服务 Demo（学习项目）
     - 项目描述：基于 Spring Cloud Alibaba 的微服务 Demo
     - 技术栈：Spring Cloud Alibaba、Nacos、Gateway、OpenFeign、Sentinel
     - 责任描述：
       - 整合 Nacos 作为注册中心与配置中心
       - 使用 Gateway 作为统一网关，实现路由转发与过滤
       - 使用 OpenFeign 实现服务间声明式调用
       - 使用 Sentinel 实现限流与降级

### 输出

- 简历（word / pdf）
- 控制在 2 页
- 突出后端能力，前端弱化

---

## 四、第 30 天目标：自我介绍与模拟面试

### 核心内容

1. **自我介绍（1～2 分钟）**
   - 基本信息：姓名、工作年限
   - 背景：之前做前端，现在转 Java 后端
   - 能力：Java 基础、Spring Boot、MySQL、Redis、RabbitMQ
   - 项目：做了权限管理后台、订单系统、微服务 Demo
   - 意愿：希望找 Java 后端相关工作

2. **项目介绍（每个 3～5 分钟）**
   - 权限后台：背景、技术栈、模块、难点、亮点
   - 订单系统：背景、技术栈、核心流程、难点、亮点
   - 微服务 Demo：组件、功能

3. **模拟面试**
   - 找朋友、或用 AI 模拟
   - 录音/录像，复盘
   - 查漏补缺

4. **常见面试问题准备**
   - 为什么从前端转后端？
   - 前端经验对后端有帮助吗？
   - 你觉得后端和前端最大的区别是什么？
   - 未来职业规划？

### 输出

- 自我介绍稿
- 项目介绍稿
- 模拟面试复盘记录

---

## 五、最终输出检查清单

- [ ] 权限后台项目代码完整，可运行
- [ ] 订单系统代码完整，可运行
- [ ] 微服务 Demo 代码完整，可运行
- [ ] 数据库设计文档
- [ ] 缓存设计文档
- [ ] 项目讲稿
- [ ] 面试题整理
- [ ] 简历
- [ ] 自我介绍稿
- [ ] GitHub 代码仓库整理好
- [ ] README 写清楚

---

## 六、投递建议

- 先投小公司练手
- 再投中等公司
- 最后投目标公司
- 每次面试后及时复盘