# 当前单体架构项目总结 (Project Context for Migration)

## 1. 项目概况
*   **项目名称**: CrudDemo (高并发秒杀系统 - 单体版)
*   **核心目标**: 解决电商大促场景下的高并发写、超卖、接口防刷等问题。
*   **当前架构**: Spring Boot 单体架构 (Monolith)。

## 2. 技术栈 (Tech Stack)
*   **语言/框架**: Java 17, Spring Boot 3.2.1
*   **ORM**: MyBatis + MyBatis-Spring-Boot-Starter
*   **数据库**: MySQL 8.0
*   **缓存**: Redis (StringRedisTemplate), Redisson (分布式锁)
*   **消息队列**: RocketMQ 5.0 (RocketMQTemplate, 事务消息)
*   **其他**: Lombok, AOP (WebLogAspect)

## 3. 数据库设计 (Database Schema)
包含三张核心表：
1.  **`user`**: 用户表 (id, username, password, email, age, create_time)
2.  **`seckill_goods`**: 秒杀商品表 (id, goods_name, stock_count)
    *   *关键点*: 库存扣减依赖数据库乐观锁 `UPDATE ... WHERE stock_count > 0`。
3.  **`seckill_order`**: 秒杀订单表 (id, user_id, goods_id, create_time)
    *   *关键点*: `(user_id, goods_id)` 唯一索引，防止数据库层面重复下单。

## 4. 核心业务模块逻辑

### 4.1 用户管理模块 (User Module)
*   **涉及类**: `UserController`, `UserServiceImpl`
*   **逻辑**: 标准 CRUD。
*   **缓存策略**: **Cache-Aside Pattern (旁路缓存)**。
    *   查询: 先查 Redis (`user:cache:{id}`) -> 无则查 DB -> 回写 Redis (30min过期)。
    *   更新/删除: 先操作 DB -> 成功后立即删除 Redis Key。

### 4.2 秒杀核心模块 (Seckill Module)
*   **涉及类**: `SeckillController`, `SeckillTransactionListener`, `SeckillConsumer`, `StockWarmup`
*   **流程**:
    1.  **库存预热**: `StockWarmup` 实现 `CommandLineRunner`，启动时将 DB 库存加载到 Redis (`seckill:stock:{id}`)。
    2.  **防刷/隐藏地址**: `getSeckillPath` 生成 MD5 签名 (`userId` + `goodsId` + `timestamp` + `salt`)。
    3.  **秒杀请求 (`/seckill/{sign}/seckill`)**:
        *   **校验**: 验签、验时效。
        *   **限流/防重**: 使用 `Redisson` 分布式锁 (`lock:user:{id}`)。
        *   **重复购买检查**: 查 Redis (`seckill:bought:{goodsId}:{userId}`)。
        *   **发送事务消息**: 调用 `rocketMQTemplate.sendMessageInTransaction`。
    4.  **RocketMQ 事务监听器 (`SeckillTransactionListener`)**:
        *   **执行本地事务**: 在这里执行 **Redis 预减库存** (`decrement`) 和 **写入购买标记**。
        *   *逻辑*: 如果 Redis 扣减成功，提交事务消息；失败则回滚。
    5.  **异步下单 (`SeckillConsumer`)**:
        *   监听 `seckill-topic`。
        *   **DB 操作**: 执行 `seckillGoodsMapper.decreaseStock` (乐观锁) 和 `seckillOrderMapper.insert`。
        *   **幂等处理**: 捕获 `DuplicateKeyException` (唯一索引冲突)，吞掉异常防止死循环重试。

## 5. 待拆分/优化方向 (Next Steps)
为了向 **Spring Cloud Alibaba** 迁移，建议拆分方案：

1.  **服务拆分**:
    *   `user-service`: 包含 `User` 相关逻辑。
    *   `order-service` (或 `seckill-service`): 包含秒杀、订单、RocketMQ 消费者逻辑。
    *   `gateway-service`: 统一网关，集成 Sentinel 限流。
2.  **组件引入**:
    *   **Nacos**: 替代 `application.properties` 做配置中心，替代硬编码 URL 做服务发现。
    *   **OpenFeign**: 用于服务间调用 (如订单服务查询用户信息)。
    *   **Sentinel**: 替代 Redisson 的简单限流，做更精细的熔断降级。
    *   **Seata** (可选): 如果涉及跨库分布式事务。
