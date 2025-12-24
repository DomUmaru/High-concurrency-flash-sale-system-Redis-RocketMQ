# 🚀 High-Concurrency Seckill System (高并发秒杀系统)

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green) ![RocketMQ](https://img.shields.io/badge/RocketMQ-5.0-blue) ![Redis](https://img.shields.io/badge/Redis-7.0-red)

> A high-performance e-commerce seckill system implementation based on Spring Boot, RocketMQ, and Redis.  
> 基于 Spring Boot + RocketMQ + Redis 构建的高性能电商秒杀系统，实现了在高并发场景下的**流量削峰**、**数据一致性**与**超卖防护**。

---

## 📖 项目简介 (Introduction)

本项目不仅仅是一个简单的 CRUD Demo，而是针对**互联网大厂高并发场景**（如双11秒杀、抢票）设计的解决方案原型。

核心目标是解决以下技术难题：
*   **高并发写**：如何防止瞬间流量击穿数据库？
*   **超卖问题**：如何在多线程并发下保证库存数据的绝对安全？
*   **接口防刷**：如何防止恶意脚本抢跑？
*   **数据一致性**：如何保证缓存（Redis）与数据库（MySQL）之间的数据最终一致性？

---

## 🛠️ 技术栈 (Tech Stack)

*   **核心框架**: Spring Boot 3.2.1
*   **ORM**: MyBatis + MyBatis-Spring-Boot-Starter
*   **数据库**: MySQL 8.0
*   **缓存中间件**: Redis (Spring Data Redis + Redisson)
*   **消息队列**: RocketMQ (事务消息 Transaction Message)
*   **工具库**: Lombok, FastJSON, Apache Commons

---

## 💡 核心架构与亮点 (Architecture & Highlights)

### 1. 流量削峰 (Traffic Shaving)
利用 **RocketMQ** 将同步下单流程改为异步化。
*   用户请求 -> Controller -> 发送 MQ 消息 -> 立即返回 "排队中"。
*   消费者 (Consumer) -> 监听 MQ -> 慢速写入数据库。
*   **效果**：将数据库的写压力从瞬间 10w QPS 降低到数据库可承受的范围。

### 2. 多级缓存与一致性 (Multi-level Caching)
*   **用户模块**：采用 **Cache-Aside Pattern (旁路缓存)** 模式。读请求优先查 Redis，写请求采用 "先更新 DB，后删除 Cache" 的策略，保证数据最终一致性。
*   **秒杀模块**：采用 **Redis 预减库存**。秒杀开始前将库存预热至 Redis，所有的扣减操作在 Redis 内存中完成，拦截 99% 的无效流量。

### 3. 分布式锁与防重 (Distributed Lock)
*   引入 **Redisson** 实现分布式锁，解决 "一人一单" 问题，防止同一用户并发重复下单。
*   利用 **RocketMQ 事务消息** 机制，将 "Redis 扣减库存" 与 "MQ 消息发送" 绑定为原子操作，确保不会出现 "Redis 扣了但消息没发" 的情况。

### 4. 安全防护 (Security)
*   **MD5 动态签名**：实现秒杀接口的地址隐藏。前端必须携带合法的 `sign` 签名（MD5(userId + goodsId + timestamp + salt)）才能请求接口，防止脚本刷单。
*   **乐观锁 (Optimistic Locking)**：数据库底层使用 `stock_count > 0` 作为兜底逻辑，彻底杜绝超卖。

---

## ⚡ 快速开始 (Quick Start)

### 1. 环境准备
确保本地已安装并启动以下服务：
*   **MySQL** (默认端口 3306)
*   **Redis** (默认端口 6379)
*   **RocketMQ** (NameServer: 9876, Broker: 10911)

### 2. 数据库初始化
请在 MySQL 中执行以下 SQL 脚本：

```sql
-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- 秒杀商品表
CREATE TABLE `seckill_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(100) DEFAULT NULL,
  `stock_count` int DEFAULT NULL COMMENT '剩余库存',
  PRIMARY KEY (`id`)
);
INSERT INTO seckill_goods (goods_name, stock_count) VALUES ('iPhone 15', 100);

-- 秒杀订单表
CREATE TABLE `seckill_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `goods_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_goods` (`user_id`, `goods_id`) -- 唯一索引防重
);
```

### 3. 修改配置
打开 `src/main/resources/application.properties`，修改你的数据库和中间件配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=root
spring.datasource.password=your_password

rocketmq.name-server=127.0.0.1:9876
```

### 4. 启动项目
运行 `CrudDemoApplication.java`。项目启动时会自动执行 `StockWarmup` 类，将数据库库存预热到 Redis 中。

---

## 🧪 接口测试 (API Testing)

### 1. 获取秒杀签名
*   **URL**: `GET /seckill/path`
*   **Params**: `goodsId=1`, `userId=1001`
*   **Response**: 
    ```json
    {
        "code": 200,
        "msg": "success",
        "data": {
            "sign": "38b9...",
            "timestamp": 1700000000000
        }
    }
    ```

### 2. 执行秒杀
*   **URL**: `POST /seckill/{sign}/seckill`
*   **Params**: `userId=1001`, `goodsId=1`, `timestamp=...`
*   **Response**:
    ```json
    {
        "code": 200,
        "msg": "success",
        "data": "排队中"
    }
    ```

---

## 📝 License

This project is licensed under the MIT License.
