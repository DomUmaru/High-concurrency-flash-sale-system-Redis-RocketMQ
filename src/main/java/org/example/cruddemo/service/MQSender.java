package org.example.cruddemo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendSeckillMessage(String message) {
        log.info("Sending seckill message: {}", message);
        rocketMQTemplate.convertAndSend("seckill-topic", message);
    }
}
