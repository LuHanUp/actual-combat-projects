package top.luhancc.gulimall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author luHan
 * @create 2021/1/26 15:54
 * @since 1.0.0
 */
@Controller
public class TestUnifyResultController {

    @RequestMapping("unify1")
    public String unify1() {
        return "unify1";
    }

    @RequestMapping("unify2")
    @ResponseBody
    public String unify2() {
        return "这是一条数据";
    }
}
