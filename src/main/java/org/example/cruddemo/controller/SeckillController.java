package org.example.cruddemo.controller;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.example.cruddemo.common.Result;
import org.example.cruddemo.entity.SeckillGoods;
import org.example.cruddemo.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 秒杀接口
     * @param goodsId 商品ID
     * @param userId 模拟的用户ID（实际开发中从 Token 获取）
     */
    @PostMapping
    public Result<String> seckill(@RequestParam Long goodsId, @RequestParam Long userId) {
        // 1. 组装消息 (格式：userId,goodsId)
        String message = userId + "," + goodsId;
        String stockKey = "seckill:stock:" + goodsId;

        Long stock = stringRedisTemplate.opsForValue().decrement(stockKey);

        // 判读库存
        if (stock < 0) {
            return Result.error("手慢了，抢光了");
        }

        // 2. 发送给 RocketMQ
        // "seckill-topic" 是我们在 MQ 里约定的频道名字
        rocketMQTemplate.convertAndSend("seckill-topic", message);

        System.out.println("【生产者】用户 " + userId + " 正在排队...");

        // 3. 极速返回，不等待数据库结果
        return Result.success("排队中，请稍后查询结果...");
    }
}
