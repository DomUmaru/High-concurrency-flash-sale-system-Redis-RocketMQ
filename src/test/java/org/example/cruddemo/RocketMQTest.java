package org.example.cruddemo;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RocketMQTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void testSend() {
        // 参数1：Topic (类似于邮箱地址)
        // 参数2：消息内容
        rocketMQTemplate.convertAndSend("test-topic", "Hello RocketMQ from ACMer!");
        System.out.println("发送成功！");
    }
}