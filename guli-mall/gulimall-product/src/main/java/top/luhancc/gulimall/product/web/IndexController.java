package top.luhancc.gulimall.product.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.service.CategoryService;
import top.luhancc.gulimall.product.vo.web.Category2Vo;

import java.util.List;
import java.util.Map;

/**
 * 首页的Controller
 *
 * @author luHan
 * @create 2021/1/5 17:52
 * @since 1.0.0
 */
@Controller
@RequiredArgsConstructor
public class IndexController {
    private final CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 1. 查询出所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevelOneCategories();
        model.addAttribute("categories", categoryEntities);
        return "index";// 会被视图解析器拼接成:prefix + index + suffix
    }


    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Category2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }
}
