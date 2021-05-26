package top.luhancc.gulimall.coupon.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.coupon.feign.fallback.ProductFallback;

/**
 * @author luHan
 * @create 2021/1/20 15:12
 * @since 1.0.0
 */
@FeignClient(value = "gulimall-product", fallback = ProductFallback.class)
public interface ProductFeign {

    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R getSkuInfoById(@PathVariable("skuId") Long skuId);


}
