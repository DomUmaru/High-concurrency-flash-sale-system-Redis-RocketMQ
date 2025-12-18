package org.example.cruddemo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.example.cruddemo.entity.SeckillOrder;
import org.example.cruddemo.mapper.SeckillGoodsMapper;
import org.example.cruddemo.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RocketMQMessageListener(topic = "seckill-topic", consumerGroup = "seckill-group")
public class SeckillConsumer implements RocketMQListener<String> {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public void onMessage(String message) {
        // 1. 解析消息 "userId,goodsId"
        String[] args = message.split(",");
        Long userId = Long.parseLong(args[0]);
        Long goodsId = Long.parseLong(args[1]);

        log.info("【消费者】开始处理用户 {} 的秒杀请求...", userId);

        // 2. 扣减库存 (执行 Step 2 生成的那个带乐观锁的 SQL)
        // 返回影响行数，rows=1 表示扣减成功，rows=0 表示库存没了
        int rows = seckillGoodsMapper.decreaseStock(goodsId);

        if (rows > 0) {
            // 3. 扣库存成功 -> 下订单
            SeckillOrder order = new SeckillOrder();
            order.setUserId(userId);
            order.setGoodsId(goodsId);
            order.setCreateTime(LocalDateTime.now());
            seckillOrderMapper.insert(order);
            log.info(">>> 恭喜！用户 {} 秒杀成功！", userId);
        } else {
            log.warn(">>> 遗憾！用户 {} 秒杀失败，库存不足", userId);
        }
    }
}