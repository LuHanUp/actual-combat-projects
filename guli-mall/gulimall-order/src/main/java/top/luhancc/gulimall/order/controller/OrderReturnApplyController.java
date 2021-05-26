package top.luhancc.gulimall.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import top.luhancc.gulimall.order.entity.OrderReturnApplyEntity;
import top.luhancc.gulimall.order.service.OrderReturnApplyService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.R;


/**
 * 订单退货申请
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:50:19
 */
@RestController
@RequestMapping("order/orderreturnapply")
public class OrderReturnApplyController {
    @Autowired
    private OrderReturnApplyService orderReturnApplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("order:orderreturnapply:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = orderReturnApplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("order:orderreturnapply:info")
    public R info(@PathVariable("id") Long id) {
            OrderReturnApplyEntity orderReturnApply = orderReturnApplyService.getById(id);

        return R.ok().put("orderReturnApply", orderReturnApply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("order:orderreturnapply:save")
    public R save(@RequestBody OrderReturnApplyEntity orderReturnApply) {
            orderReturnApplyService.save(orderReturnApply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("order:orderreturnapply:update")
    public R update(@RequestBody OrderReturnApplyEntity orderReturnApply) {
            orderReturnApplyService.updateById(orderReturnApply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("order:orderreturnapply:delete")
    public R delete(@RequestBody Long[] ids) {
            orderReturnApplyService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
