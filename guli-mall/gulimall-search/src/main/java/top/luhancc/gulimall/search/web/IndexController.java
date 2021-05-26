package top.luhancc.gulimall.search.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import top.luhancc.gulimall.search.domain.vo.web.SearchParam;
import top.luhancc.gulimall.search.domain.vo.web.SearchResult;
import top.luhancc.gulimall.search.service.SearchService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author luHan
 * @create 2021/1/7 17:20
 * @since 1.0.0
 */
@Controller
public class IndexController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/list.html")
    public String listPage(HttpServletRequest httpServletRequest, SearchParam searchParam, Model model) {
        searchParam.set_queryString(httpServletRequest.getQueryString());// 设置参数的字符串形式
        SearchResult searchResult = searchService.search(searchParam);
        model.addAttribute("result", searchResult);
        return "list";
    }
}
