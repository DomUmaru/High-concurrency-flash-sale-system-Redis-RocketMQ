package org.example.cruddemo;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.example.cruddemo.entity.User;
import org.example.cruddemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Random;

@SpringBootTest
public class UserBulkInsertTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void insertUsers() {
        // 开启批量处理模式
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        UserMapper mapper = session.getMapper(UserMapper.class);

        long startTime = System.currentTimeMillis();
        System.out.println("开始批量插入数据...");

        try {
            Random random = new Random();
            for (int i = 100001; i <= 200000; i++) {
                User user = new User();
                user.setUsername("User_" + i);
                user.setPassword("123456"); // 默认密码
                user.setEmail("user" + i + "@test.com");
                user.setAge(random.nextInt(100) + 1);
                user.setCreateTime(LocalDateTime.now());

                mapper.insert(user);

                // 每1000条提交一次
                if (i % 1000 == 0) {
                    session.commit();
                    session.clearCache();
                }
            }
            // 提交剩余的数据
            session.commit();
            session.clearCache();
        } catch (Exception e) {
            session.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("批量插入完成，总耗时：" + (endTime - startTime) + " ms");
    }
}
