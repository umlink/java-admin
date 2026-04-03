# Java 后端转岗：第 17～20 天执行手册（JVM 与并发基础）

## 🎯 总目标（4 天）

在 4 天内完成：

- JVM 内存结构学习
- GC 基础学习
- 线程基础与线程池
- synchronized、volatile、Lock
- CAS 与 ConcurrentHashMap
- CompletableFuture 异步编程

---

## 一、第 17 天目标：JVM 内存结构

### 学习内容

1. **JVM 运行时数据区**
   - 程序计数器
   - Java 虚拟机栈
   - 本地方法栈
   - 堆（Heap）
   - 方法区（元空间）
   - 运行时常量池
   - 直接内存

2. **堆内存划分**
   - 年轻代（Eden、Survivor S0、Survivor S1）
   - 老年代
   - 对象分配流程

3. **常用 JVM 参数**
   - `-Xms`、`-Xmx` 堆大小
   - `-Xmn` 年轻代大小
   - `-XX:MetaspaceSize` 元空间
   - `-XX:+PrintGCDetails` 打印 GC 日志

4. **OOM 模拟**
   - 堆 OOM：无限创建对象
   - 栈溢出：无限递归
   - 理解常见 OOM 原因

### 输出

- JVM 内存结构笔记
- 简单的 OOM 示例代码（放在 test 目录）

---

## 二、第 18 天目标：GC 基础

### 学习内容

1. **垃圾回收算法**
   - 标记-清除
   - 复制
   - 标记-整理
   - 分代回收思想

2. **垃圾收集器**
   - Serial / Serial Old
   - ParNew
   - Parallel Scavenge / Parallel Old
   - CMS（重点）
   - G1（重点）

3. **GC 日志分析**
   - 看懂 Young GC 日志
   - 看懂 Full GC 日志
   - GC 频率与耗时分析

4. **对象存活判断**
   - 引用计数法
   - 可达性分析
   - GC Roots 有哪些
   - 四种引用：强、软、弱、虚

### 输出

- GC 算法与收集器笔记
- 配置项目启动 GC 日志打印

---

## 三、第 19 天目标：线程与锁

### 学习内容

1. **线程基础**
   - 线程创建方式：Thread、Runnable、Callable
   - 线程生命周期：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED
   - 常用方法：start()、run()、sleep()、wait()、notify()、join()、interrupt()

2. **synchronized**
   - 同步方法、同步代码块
   - 锁对象：this、类对象、自定义对象
   - 可重入性
   - 锁升级：无锁 → 偏向锁 → 轻量级锁 → 重量级锁

3. **volatile**
   - 可见性
   - 禁止指令重排序
   - 不能保证原子性
   - 使用场景：状态标记、双重检查锁单例

4. **Lock 接口**
   - ReentrantLock
   - lock() / unlock()
   - tryLock()
   - Condition
   - vs synchronized

### 输出

- 线程与锁笔记
- 几个 synchronized / volatile / Lock 的示例代码

---

## 四、第 20 天目标：并发工具类

### 学习内容

1. **CAS**
   -  Compare And Swap
   - Unsafe 类
   - ABA 问题
   - AtomicInteger、AtomicLong

2. **ConcurrentHashMap**
   - 1.7 vs 1.8 实现区别
   - 分段锁 vs CAS + synchronized
   - 为什么线程安全
   - size() 计算方式

3. **线程池**
   - ThreadPoolExecutor 构造参数
   - 核心线程、最大线程、阻塞队列、拒绝策略
   - 四种拒绝策略
   - execute() vs submit()
   - 常用线程池：FixedThreadPool、CachedThreadPool、ScheduledThreadPool、SingleThreadExecutor
   - 为什么不建议用 Executors 创建线程池

4. **CompletableFuture**
   - 异步编排
   - supplyAsync()、runAsync()
   - thenApply()、thenAccept()、thenRun()
   - thenCompose()、thenCombine()
   - allOf()、anyOf()

5. **其他并发工具**
   - CountDownLatch
   - CyclicBarrier
   - Semaphore
   - ThreadLocal

### 输出

- 并发工具类笔记
- 线程池示例
- CompletableFuture 异步编排示例

---

## 五、重点学习点

1. **JVM 内存区域**
   - 每个区域作用
   - 哪些是线程私有的，哪些是共享的

2. **GC**
   - 分代回收
   - 常用垃圾收集器特点

3. **锁**
   - synchronized 原理
   - volatile 原理
   - Lock 使用场景

4. **并发容器**
   - ConcurrentHashMap 线程安全实现

5. **线程池**
   - 参数含义
   - 工作流程

---

## 六、面试点整理（必背）

1. JVM 内存结构说一下？
2. 堆内存怎么划分的？
3. GC 算法有哪些？
4. CMS 和 G1 区别？
5. 什么是 GC Roots？
6. 强引用、软引用、弱引用、虚引用区别？
7. 创建线程有几种方式？
8. 线程生命周期？
9. sleep() 和 wait() 区别？
10. synchronized 底层原理？锁升级？
11. volatile 作用？能保证原子性吗？
12. synchronized 和 ReentrantLock 区别？
13. CAS 是什么？ABA 问题？
14. ConcurrentHashMap 1.7 和 1.8 区别？
15. 线程池参数有哪些？工作流程？
16. 为什么不建议用 Executors 创建线程池？
17. ThreadLocal 是什么？内存泄漏？
18. CountDownLatch 和 CyclicBarrier 区别？