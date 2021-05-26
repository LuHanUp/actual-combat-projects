package top.luhancc.common.to.coupon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author luHan
 * @create 2021/1/21 11:07
 * @since 1.0.0
 */
@Data
public class SeckillOrderTo {
    private String orderSn;// 订单号
    private Long memberId;// 用户id
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

    private Integer num;// 购买数量
}
