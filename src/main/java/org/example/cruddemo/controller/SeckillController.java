package org.example.cruddemo.controller;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.example.cruddemo.common.MD5Util;
import org.example.cruddemo.common.Result;
import org.example.cruddemo.entity.SeckillGoods;
import org.example.cruddemo.entity.SeckillOrder;
import org.example.cruddemo.mapper.SeckillGoodsMapper;
import org.example.cruddemo.mapper.SeckillOrderMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    // 获取秒杀地址接口
    @GetMapping("/path")
    public Result<String> getSeckillPath(@RequestParam Long goodsId, @RequestParam Long userId) {
        // 1. 生成随机加密串
        String str = MD5Util.createSeckillPath(userId, goodsId);

        // 2. 存入 Redis (设置有效期60秒)
        // 为什么存 Redis？因为一会儿秒杀的时候，我要拿前端传来的跟 Redis 里的比对！
        // Key: seckill:path:userId:goodsId
        String key = "seckill:path:" + userId + ":" + goodsId;
        stringRedisTemplate.opsForValue().set(key, str, 60, TimeUnit.SECONDS);

        return Result.success(str);
    }

    // 模拟运营后台：发布秒杀活动，同步库存到 Redis
    @PostMapping("/publish")
    public Result<String> publishSeckill(@RequestParam Long goodsId) {
        // 1. 查数据库
        SeckillGoods goods = seckillGoodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.error("商品不存在");
        }

        // 2. 写入 Redis
        String key = "seckill:stock:" + goodsId;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(goods.getStockCount()));

        // 3. (进阶) 这里还可以把用来做一人一单的 Redis Set 也清空一下，方便测试
        //stringRedisTemplate.delete("seckill:bought:" + goodsId); // 假设是 Set 结构的话

        return Result.success("活动发布成功，库存已热加载至 Redis");
    }

    /**
     * 秒杀接口
     * @param goodsId 商品ID
     * @param userId 模拟的用户ID（实际开发中从 Token 获取）
     */
    @PostMapping("/{path}/seckill")
    public Result<String> seckill(@PathVariable String path,
                                  @RequestParam Long goodsId,
                                  @RequestParam Long userId) {
        //判断秒杀路径是否正确
        String pathKey = "seckill:path:" + userId + ":" + goodsId;
        String realPath = stringRedisTemplate.opsForValue().get(pathKey);
        if (realPath == null || !realPath.equals(path)) {
            return Result.error("非法请求，请重新获取秒杀地址！");
        }

        //redisson分布式锁是为了解决一人一单问题
        String lockKey = "lock:user:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(0, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return Result.error("系统繁忙");
        }

        if (!isLocked) {
            return Result.error("操作太频繁，请稍后再试！");
        }
//        没有MQ事务管理的代码
//        try {
//            //double check 数据库查询兜底判断是否超卖
//            //可以用redis来判断是否购买，但是需要用MQ回滚保证不会死锁
//            int count = seckillOrderMapper.countByUserIdAndGoodsId(userId, goodsId);
//            if (count > 0) {
//                return Result.error("每日限定购买一件！");
//            }
//
//            // 1. 组装消息 (格式：userId,goodsId)
//            String message = userId + "," + goodsId;
//            String stockKey = "seckill:stock:" + goodsId;
//
//            Long stock = stringRedisTemplate.opsForValue().decrement(stockKey);
//
//            // 判读库存
//            if (stock < 0) {
//                return Result.error("手慢了，抢光了");
//            }
//
//            // 2. 发送给 RocketMQ
//            // "seckill-topic" 是我们在 MQ 里约定的频道名字
//            rocketMQTemplate.convertAndSend("seckill-topic", message);
//
//            System.out.println("【生产者】用户 " + userId + " 正在排队...");
//
//            // 3. 极速返回，不等待数据库结果
//            return Result.success("排队中，请稍后查询结果...");
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
        String stockKey = "seckill:stock:" + goodsId;
        String boughtKey = "seckill:bought:" + goodsId + ":" + userId;
        String msgBody = userId + "," + goodsId;

        Message<String> message = MessageBuilder.withPayload(msgBody).build();

        try {
            rocketMQTemplate.sendMessageInTransaction("seckill-topic", message, new Object[]{userId, goodsId, stockKey, boughtKey});
            return Result.success("排队中");
        }catch (Exception e) {
            return Result.error("系统繁忙");
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
