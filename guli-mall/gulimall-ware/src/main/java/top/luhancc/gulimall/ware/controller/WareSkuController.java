package top.luhancc.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.luhancc.gulimall.ware.domain.vo.LockStockResult;
import top.luhancc.gulimall.ware.domain.vo.WareSkuLockVo;
import top.luhancc.gulimall.ware.entity.WareSkuEntity;
import top.luhancc.gulimall.ware.exceptions.NoStockException;
import top.luhancc.gulimall.ware.service.WareSkuService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.R;


/**
 * 商品库存
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:53:26
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/order/lock")
    public R orderLockSku(WareSkuLockVo wareSkuLockVo) {
        try {
            Boolean lockStockResults = wareSkuService.orderLockSku(wareSkuLockVo);
            if (lockStockResults) {
                return R.ok();
            } else {
                return R.error(21000, "商品库存不足");
            }
        } catch (NoStockException e) {
            return R.error(21000, "商品[" + e.getSkuId() + "]库存不足");
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
