package top.luhancc.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import top.luhancc.gulimall.coupon.entity.CouponHistoryEntity;
import top.luhancc.gulimall.coupon.service.CouponHistoryService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.R;


/**
 * 优惠券领取历史记录
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@RestController
@RequestMapping("coupon/couponhistory")
public class CouponHistoryController {
    @Autowired
    private CouponHistoryService couponHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("coupon:couponhistory:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = couponHistoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("coupon:couponhistory:info")
    public R info(@PathVariable("id") Long id) {
            CouponHistoryEntity couponHistory = couponHistoryService.getById(id);

        return R.ok().put("couponHistory", couponHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("coupon:couponhistory:save")
    public R save(@RequestBody CouponHistoryEntity couponHistory) {
            couponHistoryService.save(couponHistory);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("coupon:couponhistory:update")
    public R update(@RequestBody CouponHistoryEntity couponHistory) {
            couponHistoryService.updateById(couponHistory);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("coupon:couponhistory:delete")
    public R delete(@RequestBody Long[] ids) {
            couponHistoryService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
