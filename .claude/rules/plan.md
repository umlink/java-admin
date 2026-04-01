# 基础约束规则和前置信息

- 项目进度完成按照计划进行
- 项目需求计划在项目下 `plan-doc ` 文件夹中, `plan-tree.md` 为工作大纲
- 编码前不能新增需求外的代码
- 稍微复杂的逻辑需要自动添加注释信息
- 项目 sql 文件位置 `src/main/resources/sql/init.sql`
- 项目 sql 初始化数据位置 `src/main/resources/sql/init-data.sql`
- 代码逻辑需要考虑各种边界情况
- 每完成一次任务需要记录整体计划的进度和当前大计划下未完成的内容输出到 `plan-doc/process.md`
- 大计划的定义为 `plan-doc` 目录下格式为 `play-${数字}.md` 开头的文件，数字顺序即为计划顺序

# 编码规则

- 技术实现按 java JDK 17、spring boot 3 的标准写
- 代码实现用 Spring Boot 3 推荐的写法
- 变量命名严格按照 java 规范
- 编码过程中需要严格考虑各种逻辑边界和代码报错边界问题
- 编码过程中需要严格考虑安全问题
- 编码在保证逻辑实现的前提下就足够精简、高级、高效
- 编码过程中要严格考虑内存泄露风险
- 不要在用 @Override 了，使用 spring boot 3推荐的 @RequiredArgsConstructor + final 字段。
- 对于包中 api 和方法的使用需要考虑是否废弃如：（DTO 中 使用 requiredMode = Schema.RequiredMode.REQUIRED 来替换 required = true)

[//]: # (- 每次完成一项功能时，需要跟需求说明确认逻辑实现是否遗漏，功能必须正常)

## 进度更新规范（文件：`plan-doc/process.md`）

-  主要记录大计划进度和当前进行中大计划的详细小计划进度
- 其他任务进度内容可以临时记录，但完成后需要删除
- 每次任务执行后更新，小任务的完成项不需要持续记录
- 历史已完成项目自动清理
- 对话中出现记录进度信息是，自动更新
- 收到更新进度任务时，清理完成的小任务记录