package top.luhancc.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.luhancc.gulimall.order.domain.order.vo.MemberAddressVo;

import java.util.List;

/**
 * @author luHan
 * @create 2021/1/14 14:45
 * @since 1.0.0
 */
@FeignClient("gulimall-member")
public interface MemberFeign {
    @GetMapping("/member/memberreceiveaddress/address/{userId}")
    public List<MemberAddressVo> getAddressById(@PathVariable("userId") Long userId);
}
