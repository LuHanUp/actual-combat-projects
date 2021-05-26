package top.luhancc.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.luhancc.common.utils.R;

/**
 * @author luHan
 * @create 2021/1/14 16:40
 * @since 1.0.0
 */
@FeignClient("gulimall-member")
public interface MemberFeign {
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    // @RequiresPermissions("member:memberreceiveaddress:info")
    public R info(@PathVariable("id") Long id);
}
