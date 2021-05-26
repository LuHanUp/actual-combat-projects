package top.luhancc.gulimall.product.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.luhancc.gulimall.product.service.SkuInfoService;
import top.luhancc.gulimall.product.vo.web.SkuItemVo;

/**
 * 商品的详情信息Controller
 *
 * @author luHan
 * @create 2021/1/5 17:52
 * @since 1.0.0
 */
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final SkuInfoService skuInfoService;

    @GetMapping({"/{skuId}.html"})
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        SkuItemVo skuItemVo = skuInfoService.itemDetail(skuId);
        model.addAttribute("item", skuItemVo);
        return "item";// 会被视图解析器拼接成:prefix + index + suffix
    }
}
