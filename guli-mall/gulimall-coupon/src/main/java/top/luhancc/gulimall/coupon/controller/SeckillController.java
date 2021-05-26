package top.luhancc.gulimall.coupon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.coupon.domain.to.SeckillSkuRedisTo;
import top.luhancc.gulimall.coupon.feign.ProductFeign;
import top.luhancc.gulimall.coupon.service.seckill.SeckillService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/1/20 15:56
 * @since 1.0.0
 */
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private ProductFeign productFeign;

    @GetMapping("/fallback/{skuId}")
    public R testFeignFallback(@PathVariable("skuId") Long skuId) {
        return productFeign.getSkuInfoById(skuId);
    }

    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillSkus();
        return R.ok(data);
    }

    @GetMapping("/getSkuSeckillInfo")
    public R getSkuSeckillInfo(@RequestParam("skuId") Long skuId) {
        SeckillSkuRedisTo seckillSkuRedisTo = seckillService.getSkuSeckillInfo(skuId);
        return R.ok(seckillSkuRedisTo);
    }

    @GetMapping("/kill")
    public R seckill(@RequestParam("killId") String killId,
                     @RequestParam("code") String code,
                     @RequestParam("num") Integer num) {
        String orderSn = seckillService.seckill(killId, code, num);
        return R.ok(orderSn);
    }
}
