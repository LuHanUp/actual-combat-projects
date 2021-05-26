package top.luhancc.gulimall.coupon.service.seckill;

import top.luhancc.gulimall.coupon.domain.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author luHan
 * @create 2021/1/20 13:46
 * @since 1.0.0
 */
public interface SeckillService {
    /**
     * 上架最近3天需要参与秒杀的商品
     */
    void uploadSeckillSkuLatest3Day();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * 商品秒杀
     *
     * @param killId
     * @param code
     * @param num
     * @return 秒杀成功返回订单号
     */
    String seckill(String killId, String code, Integer num);
}
