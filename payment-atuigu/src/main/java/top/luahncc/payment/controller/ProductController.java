package top.luahncc.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luahncc.payment.domain.vo.R;
import top.luahncc.payment.service.ProductService;

import javax.annotation.Resource;

/**
 * @author luHan
 * @create 2022/1/9 18:56
 * @since 1.0.0
 */
@RestController
@RequestMapping("api/product")
public class ProductController {
    @Resource
    private ProductService productService;

    @GetMapping("/list")
    public R list() {
        return R.ok().data("productList", productService.list());
    }
}
