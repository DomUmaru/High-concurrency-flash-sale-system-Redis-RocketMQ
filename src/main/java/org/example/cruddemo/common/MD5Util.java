package org.example.cruddemo.common;

import org.springframework.util.DigestUtils;
import java.util.UUID;

public class MD5Util {

    // 生成随机的 Salt (盐)，防止被彩虹表破解
    private static final String SALT = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5DigestAsHex(src.getBytes());
    }

    /**
     * 生成秒杀路径
     * 逻辑：MD5(UUID + 固定Salt)
     */
    public static String createSeckillPath(Long userId, Long goodsId) {
        String str = userId + "_" + goodsId + "_" + UUID.randomUUID() + SALT;
        return md5(str);
    }
}