package top.luhancc.sso.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author luHan
 * @create 2021/1/12 18:12
 * @since 1.0.0
 */
@Controller
public class LoginController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/login.html")
    public String loginPage(
            @RequestParam("redirectUrl") String redirectUrl,
            @CookieValue(value = "sso_token", required = false) String token,
            Model model) {
        if (!StringUtils.isEmpty(token)) {
            // 说明之前有人登录过,留下了痕迹
            return "redirect:" + redirectUrl + "?token=" + token;
        }
        model.addAttribute("redirectUrl", redirectUrl);// 将需要跳转的地址保存起来,避免login接口获取不到导致登录成功后无法跳转到之前的页面
        return "login";
    }

    @PostMapping("/doLogin")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("redirectUrl") String redirectUrl,
                        HttpServletResponse response) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            // 登录成功后将用户保存起来
            String token = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(token, username);
            // 给浏览器留一个登录过的记号
            response.addCookie(new Cookie("sso_token", token));
            // 登录成功后跳转到之前的页面
            return "redirect:" + redirectUrl + "?token=" + token;
        }
        return "login";
    }

    @GetMapping("/getUserName")
    @ResponseBody
    public String getUserName(@RequestParam("token") String token) {
        return redisTemplate.opsForValue().get(token);
    }
}
