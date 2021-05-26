package top.luhancc.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.luhancc.common.utils.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luHan
 * @create 2021/1/13 13:56
 * @since 1.0.0
 */
@FeignClient("gulimall-product")
public interface ProductFeign {

    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R getSkuInfoById(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/skuinfo/stringlist/{skuId}")
    public List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skuinfo/getPrice/{skuId}")
    public BigDecimal getPriceBySkuId(@PathVariable("skuId") Long skuId);
}
