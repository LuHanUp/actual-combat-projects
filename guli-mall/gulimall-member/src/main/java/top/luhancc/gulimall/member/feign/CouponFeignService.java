package top.luhancc.gulimall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import top.luhancc.common.utils.R;

/**
 * @author luHan
 * @create 2020/12/8 11:23
 * @since 1.0.0
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @RequestMapping("coupon/coupon/member.list")
    public R memberCoupons();
}
