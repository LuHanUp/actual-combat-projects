package top.luhancc.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import top.luhancc.gulimall.product.entity.ProductAttrValueEntity;
import top.luhancc.gulimall.product.service.ProductAttrValueService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.R;


/**
 * spu属性值
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
@RestController
@RequestMapping("product/productattrvalue")
public class ProductAttrValueController {
    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:productattrvalue:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = productAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:productattrvalue:info")
    public R info(@PathVariable("id") Long id) {
            ProductAttrValueEntity productAttrValue = productAttrValueService.getById(id);

        return R.ok().put("productAttrValue", productAttrValue);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:productattrvalue:save")
    public R save(@RequestBody ProductAttrValueEntity productAttrValue) {
            productAttrValueService.save(productAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:productattrvalue:update")
    public R update(@RequestBody ProductAttrValueEntity productAttrValue) {
            productAttrValueService.updateById(productAttrValue);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:productattrvalue:delete")
    public R delete(@RequestBody Long[] ids) {
            productAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
