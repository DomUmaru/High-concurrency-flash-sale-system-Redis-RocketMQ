package org.example.cruddemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        // 1. 存数据 (相当于 map["hello"] = "world")
        // opsForValue() 是操作 String 类型的接口
        stringRedisTemplate.opsForValue().set("acm", "Java is Good");

        // 2. 取数据 (相当于 cout << map["hello"])
        String value = stringRedisTemplate.opsForValue().get("acm");

        System.out.println("========================================");
        System.out.println("从 Redis 读到的数据: " + value);
        System.out.println("========================================");
    }
}