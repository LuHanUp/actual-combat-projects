package top.luhancc.gulimall.coupon.domain.to;

import lombok.Data;
import top.luhancc.gulimall.coupon.domain.vo.SeckillSkuInfoVo;

import java.math.BigDecimal;

/**
 * @author luHan
 * @create 2021/1/20 15:08
 * @since 1.0.0
 */
@Data
public class SeckillSkuRedisTo {
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
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
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

    /**
     * sku基本信息
     */
    private SeckillSkuInfoVo skuInfo;
}
