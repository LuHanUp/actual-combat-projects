package top.luhancc.gulimall.coupon.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.coupon.feign.ProductFeign;

/**
 * @author luHan
 * @create 2021/1/21 15:49
 * @since 1.0.0
 */
@Slf4j
@Component
public class ProductFallback implements ProductFeign {
    @Override
    public R getSkuInfoById(Long skuId) {
        log.warn("ProductFeign.getSkuInfoById接口熔断了,skuId:{}", skuId);
        return R.error(429, "熔断了");
    }
}
