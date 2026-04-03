# Java 后端转岗：第 21～24 天执行手册（微服务基础）

## 🎯 总目标（4 天）

在 4 天内完成：

- Spring Cloud Alibaba 基础认知
- Nacos 注册中心
- Nacos 配置中心
- Gateway 网关
- OpenFeign 服务间调用
- Sentinel 限流基础

---

## 一、第 21 天目标：微服务概念与 Nacos 注册中心

### 学习内容

1. **微服务基础概念**
   - 单体 vs 微服务
   - 微服务优缺点
   - Spring Cloud vs Spring Cloud Alibaba
   - 核心组件：注册中心、配置中心、网关、服务调用、限流熔断

2. **Nacos 注册中心 - 部署**
   - 下载 Nacos
   - 单机启动 Nacos
   - 访问 Nacos 控制台

3. **创建微服务项目结构**
   - 新建父工程 `micro-demo`（或新建模块）
   - 子模块：`user-service`、`product-service`、`order-service`、`gateway`
   - 统一依赖管理

4. **服务注册到 Nacos**
   - 引入 `spring-cloud-starter-alibaba-nacos-discovery`
   - 配置 `application.yaml` Nacos 地址
   - 启动 `user-service`、`product-service`
   - 在 Nacos 控制台看到服务注册成功

### 输出

- 微服务基础笔记
- 可运行的 2 个服务并注册到 Nacos

---

## 二、第 22 天目标：Nacos 配置中心与 OpenFeign

### 学习内容

1. **Nacos 配置中心**
   - 引入 `spring-cloud-starter-alibaba-nacos-config`
   - 在 Nacos 控制台创建配置
   - 服务读取 Nacos 配置
   - 配置动态刷新 `@RefreshScope`
   - 命名空间、分组、Data ID 概念

2. **OpenFeign 服务间调用**
   - 引入 `spring-cloud-starter-openfeign`
   - 在 `order-service` 创建 `UserClient` 接口
   - `@FeignClient` 注解
   - 调用 `user-service` 的接口
   - 传递参数、Header

3. **服务调用测试**
   - `order-service` 调用 `user-service` 获取用户信息
   - 验证调用成功

### 输出

- Nacos 配置笔记
- OpenFeign 调用示例

---

## 三、第 23 天目标：Gateway 网关

### 学习内容

1. **Gateway 基础**
   - 引入 `spring-cloud-starter-gateway`
   - 核心概念：Route、Predicate、Filter
   - 配置路由规则

2. **路由配置**
   - 路径断言：`Path=/api/user/**` → 转发到 `user-service`
   - 路径断言：`Path=/api/product/**` → 转发到 `product-service`
   - 路径断言：`Path=/api/order/**` → 转发到 `order-service`
   - StripPrefix 去掉前缀

3. **Filter**
   - 添加请求头 Filter
   - 添加响应头 Filter
   - 全局 Filter：统计请求耗时
   - 自定义 GatewayFilter

4. **网关调用测试**
   - 通过网关调用各个服务
   - 验证路由转发正确

### 输出

- Gateway 笔记
- 可运行的网关服务

---

## 四、第 24 天目标：Sentinel 限流

### 学习内容

1. **Sentinel 基础**
   - 引入 `spring-cloud-starter-alibaba-sentinel`
   - 下载并启动 Sentinel 控制台
   - 配置 Sentinel 控制台地址
   - 访问服务接口后在 Sentinel 控制台看到监控

2. **限流规则**
   - QPS 限流
   - 线程数限流
   - 流控模式：直接、关联、链路
   - 流控效果：快速失败、Warm Up、排队等待

3. **降级规则**
   - 慢调用比例
   - 异常比例
   - 异常数

4. **热点参数限流**
   - `@SentinelResource` 注解
   - 热点参数规则配置

5. **系统保护规则**
   - Load
   - RT
   - 线程数
   - 入口 QPS
   - CPU 使用率

### 输出

- Sentinel 笔记
- 限流、降级示例

---

## 五、重点学习点

1. **注册中心**
   - 服务注册与发现
   - Nacos 核心功能

2. **配置中心**
   - 配置集中管理
   - 动态刷新

3. **网关**
   - 统一入口
   - 路由转发
   - 过滤

4. **服务调用**
   - OpenFeign 声明式调用

5. **限流熔断**
   - Sentinel 限流规则
   - 降级规则

---

## 六、面试点整理

1. 微服务优缺点？
2. Spring Cloud 和 Spring Cloud Alibaba 区别？
3. Nacos 作用？CAP 理论中属于 AP 还是 CP？
4. Nacos 如何做配置动态刷新？
5. Gateway 核心概念？
6. Gateway 过滤器有哪些？
7. OpenFeign 原理？
8. Sentinel 限流规则有哪些？
9. 限流算法：令牌桶 vs 漏桶？
10. 服务雪崩怎么解决？