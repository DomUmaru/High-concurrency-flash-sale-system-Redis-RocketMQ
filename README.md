# User Management CRUD 练习指引

你好！这是一套专门为你准备的 **Spring Boot + MyBatis** 练习骨架。
你的目标是完善这个项目，实现对用户的增删改查功能。

## ?? 准备工作 (Step 0)

在开始写 Java 代码之前，你需要先配置好环境。

### 1. 添加依赖 (`pom.xml`)
目前的 `pom.xml` 可能缺少必要的库，请确保添加以下依赖：
*   **Lombok**: 用于简化 Getter/Setter。
*   **MyBatis Spring Boot Starter**: 用于数据库操作。
*   **MySQL Driver** (或其他数据库驱动): 用于连接数据库。

```xml
<!-- 示例依赖 -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. 准备数据库
请在你的数据库中执行以下 SQL 建表语句：

```sql
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(30) DEFAULT NULL COMMENT '姓名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. 配置数据库连接 (`application.properties`)
记得配置 `spring.datasource.url`, `username`, `password` 等信息。
同时指定 Mapper XML 的位置：
```properties
mybatis.mapper-locations=classpath:mapper/*.xml
```

---

## ? 开发步骤 (你的任务)

请按照以下顺序填充代码：

### Step 1: 实现 Mapper XML
*   **文件**: `src/main/resources/mapper/UserMapper.xml`
*   **任务**: 找到 `TODO` 标签，编写具体的 SQL 语句 (`INSERT`, `SELECT`, `UPDATE`, `DELETE`)。
*   **提示**: 注意 `resultType` 和参数占位符 `#{}` 的使用。

### Step 2: 实现 Service 层
*   **文件**: `src/main/java/.../service/impl/UserServiceImpl.java`
*   **任务**: 实现接口中定义的方法。
*   **核心逻辑**:
    *   **UserDTO -> User**: 当 Controller 传来数据时，需要把 DTO 转成 Entity 才能存入数据库。
    *   **User -> UserVO**: 当从数据库查出数据时，需要把 Entity 转成 VO 再返回给 Controller（注意不要把 `password` 泄露给 VO）。
    *   **调用 Mapper**: 使用 `userMapper` 执行数据库操作。

### Step 3: 实现 Controller 层
*   **文件**: `src/main/java/.../controller/UserController.java`
*   **任务**: 补全方法体，调用 Service 层的方法，并返回 `ResponseEntity`。

### Step 4: 测试
*   启动 `CrudDemoApplication`。
*   使用 Postman 或 Apifox 测试接口：
    *   POST `http://localhost:8080/users` (添加)
    *   GET `http://localhost:8080/users` (列表)
    *   ...

---

## ? 知识点：DTO, Entity, VO 的流转

为了解耦和安全，我们在不同层使用不同的对象：

1.  **前端 -> Controller**: 传入 **DTO** (Data Transfer Object)。包含用户填写的表单数据（如用户名、密码）。
2.  **Controller -> Service**: 传递 **DTO**。
3.  **Service 内部**:
    *   将 **DTO** 转换为 **Entity** (对应数据库表结构)。
    *   调用 Mapper 保存 **Entity**。
    *   从 Mapper 获取 **Entity**。
    *   将 **Entity** 转换为 **VO** (View Object)。**VO** 是专门给前端看的，**绝对不能包含 password**！
4.  **Service -> Controller**: 返回 **VO**。
5.  **Controller -> 前端**: 返回 **VO** (JSON格式)。

加油！完成这个练习后，你对 Spring Boot CRUD 的流程就会非常熟悉了。
