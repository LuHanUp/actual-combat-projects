package top.luhancc.gulimall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.luhancc.common.utils.R;

/**
 * @author luHan
 * @create 2021/1/20 16:52
 * @since 1.0.0
 */
@FeignClient("gulimall-coupon")
public interface SeckillFeign {
    @GetMapping("/getSkuSeckillInfo")
    public R getSkuSeckillInfo(@RequestParam("skuId") Long skuId);
}
