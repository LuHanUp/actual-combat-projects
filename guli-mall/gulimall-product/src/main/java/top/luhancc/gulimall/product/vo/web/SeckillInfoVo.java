package top.luhancc.gulimall.product.vo.web;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author luHan
 * @create 2021/1/20 16:57
 * @since 1.0.0
 */
@Data
public class SeckillInfoVo {
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    /**
     * 秒杀活动开始时间
     */
    private Long startTime;

    /**
     * 秒杀活动结束时间
     */
    private Long endTime;

    /**
     * 秒杀随机码
     */
    private String randomCode;
}
