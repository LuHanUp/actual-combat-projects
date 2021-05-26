package top.luhancc.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import top.luhancc.common.utils.R;

/**
 * @author luHan
 * @create 2021/1/19 11:34
 * @since 1.0.0
 */
@FeignClient("gulimall-order")
public interface OrderFeign {

    @GetMapping("/order/order/status/{orderSn}")
    public R getOrderStatus(String orderSn);
}
