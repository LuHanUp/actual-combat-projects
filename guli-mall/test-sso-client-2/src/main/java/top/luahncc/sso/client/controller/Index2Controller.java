package top.luahncc.sso.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luHan
 * @create 2021/1/12 18:15
 * @since 1.0.0
 */
@Controller
public class Index2Controller {
    @Value("${sso.server.url}")
    private String loginUrl;

    @GetMapping("/list2")
    public String list2(@RequestParam(value = "token", required = false) String token, Model model, HttpSession session) {
        if (!StringUtils.isEmpty(token)) {
            // 获取用户信息后将其保存到session中
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://sso.server.com:8080/getUserName?token=" + token, String.class);
            session.setAttribute("user", responseEntity.getBody());
        }

        Object user = session.getAttribute("user");
        if (user == null) {
            // 跳转到登录
            return "redirect:" + loginUrl + "?redirectUrl=http://client2.com:8082/list2";
        } else {
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            emps.add("王五");
            emps.add("赵六");
            emps.add("马奇");
            model.addAttribute("emps", emps);
            return "list2";
        }
    }
}
