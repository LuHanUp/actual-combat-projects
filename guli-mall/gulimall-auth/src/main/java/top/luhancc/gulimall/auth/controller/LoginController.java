package top.luhancc.gulimall.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.luhancc.common.bo.auth.MemberInfoBo;
import top.luhancc.common.constant.AuthConstant;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.auth.domain.vo.LoginVo;
import top.luhancc.gulimall.auth.domain.vo.RegisterVo;
import top.luhancc.gulimall.auth.service.SmsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 如果一个方法只处理跳转页面，那么可以使用
 * Spring mvc viewControllers来处理，而不用一个方法来处理
 * <p>
 * 使用方法参考：{@link top.luhancc.gulimall.auth.config.WebConfig#addViewControllers(ViewControllerRegistry)}
 *
 * @author luHan
 * @create 2021/1/11 14:10
 * @since 1.0.0
 */
@Controller
@Slf4j
public class LoginController {
    @Autowired
    private SmsService smsService;

    @ResponseBody
    @PostMapping("/sendCode")
    public void sendCode(@RequestParam("phone") String phone) {
        smsService.sendCode(phone);
    }

    @PostMapping("/regist")
    @ResponseBody
    public R register(@RequestBody RegisterVo registerVo, RedirectAttributes attributes) {
        return smsService.register(registerVo);
    }

    @PostMapping("/login")
    public String login(LoginVo loginVo, HttpSession session, RedirectAttributes redirectAttributes) {
        R r = smsService.login(loginVo);
        if (r.isSuccess()) {
            MemberInfoBo memberInfoBo = r.get(MemberInfoBo.class);
            session.setAttribute(AuthConstant.LOGIN_USER_SESSION, memberInfoBo);
            log.info("用户【{}】普通登录方式成功", loginVo.getUseracct());
            String source_url = (String) session.getAttribute("source_url");
            session.removeAttribute("source_url");
            if (StringUtils.hasText(source_url)) {
                return "redirect:" + source_url;
            }
            return "redirect:http://gulimall.com";
        } else {
            redirectAttributes.addAttribute("msg", r.getMsg());
            log.error("登录失败,返回登录页【请查看gulimall-member日志】");
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

    @GetMapping("/login.html")
    public String loginPage(HttpServletRequest request, HttpSession session) {
        Object user = session.getAttribute(AuthConstant.LOGIN_USER_SESSION);
        if (user == null) {
            return "login";
        }
        String source_url = (String) session.getAttribute("source_url");
        session.removeAttribute("source_url");
        if (StringUtils.hasText(source_url)) {
            return "redirect:" + source_url;
        }
        return "redirect:http://gulimall.com";
    }

    @GetMapping("/register.html")
    public String regPage() {
        return "register";
    }
}
