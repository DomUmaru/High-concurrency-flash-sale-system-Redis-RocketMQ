package org.example.cruddemo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RocketMQMessageListener(topic = "seckill-topic", consumerGroup = "seckill-consumer")
public class MQReceiver implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("Received seckill message: {}", message);
    }
}
