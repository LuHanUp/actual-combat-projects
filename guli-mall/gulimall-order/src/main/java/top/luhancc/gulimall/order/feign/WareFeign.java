package top.luhancc.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.order.domain.order.vo.WareSkuLockVo;

/**
 * 类功能简述
 * <p>类描述</p>
 *
 * @author luHan
 * @create 2021/1/14 18:43
 * @since 1.0.0
 */
@FeignClient("gulimall-ware")
public interface WareFeign {
    @GetMapping("/ware/wareinfo/fare/{addrId}")
    public R getFare(@PathVariable("addrId") Long addrId);

    @RequestMapping("/ware/wareinfo/info/{id}")
    // @RequiresPermissions("ware:wareinfo:info")
    public R info(@PathVariable("id") Long id);

    @PostMapping("/ware/waresku/order/lock")
    public R orderLockSku(WareSkuLockVo wareSkuLockVo);
}
