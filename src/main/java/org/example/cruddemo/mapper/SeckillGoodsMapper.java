package org.example.cruddemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.cruddemo.entity.SeckillGoods;

@Mapper
public interface SeckillGoodsMapper {
    /**
     * 扣减库存
     * @param id 商品ID
     * @return 影响行数
     */
    int decreaseStock(Long id);

    /**
     * 根据ID查询
     * @param id 商品ID
     * @return 商品信息
     */
    SeckillGoods selectById(Long id);
}
