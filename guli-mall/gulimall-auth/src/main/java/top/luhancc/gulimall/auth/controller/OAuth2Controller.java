package top.luhancc.gulimall.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.luhancc.common.component.HttpComponent;
import top.luhancc.common.constant.AuthConstant;
import top.luhancc.common.to.member.SocialUserTo;
import top.luhancc.common.utils.R;
import top.luhancc.common.bo.auth.MemberInfoBo;
import top.luhancc.gulimall.auth.domain.vo.WeiboAccessTokenVo;
import top.luhancc.gulimall.auth.feign.MemberFeign;
import top.luhancc.gulimall.auth.service.OAuthService;
import top.luhancc.gulimall.auth.service.impl.auth.WeiboOAuthServiceImpl;

import javax.servlet.http.HttpSession;

/**
 * 处理社交登录的请求
 *
 * @author luHan
 * @create 2021/1/12 09:49
 * @since 1.0.0
 */
@RequestMapping("/oauth2/")
@Controller
@Slf4j
public class OAuth2Controller {
    @Autowired
    private HttpComponent httpComponent;
    @Autowired
    private MemberFeign memberFeign;

    /**
     * 微博授权成功的回调
     *
     * @param code 微博回传的code码
     * @return
     */
    @RequestMapping("/weibo/success")
    public String weiboAuth(@RequestParam("code") String code, HttpSession session) {
        // 用code换取accessToken
        String url = "https://api.weibo.com/oauth2/access_token";

        MultiValueMap<String, String> uriVariable = new LinkedMultiValueMap<>(5);
        uriVariable.add("client_id", "2793348626");
        uriVariable.add("client_secret", "5a10ad84dfaf45b5f92d39952ea357b3");
        uriVariable.add("grant_type", "authorization_code");
        uriVariable.add("redirect_uri", "http://auth.gulimall.com/oauth2/weibo/success");
        uriVariable.add("code", code);
        // https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
        ResponseEntity<WeiboAccessTokenVo> responseEntity = httpComponent.post(url, uriVariable, WeiboAccessTokenVo.class);

        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            // 授权成功就跳转回首页
            WeiboAccessTokenVo weiboAccessTokenVo = responseEntity.getBody();
            OAuthService oAuthService = new WeiboOAuthServiceImpl(weiboAccessTokenVo.getAccess_token(), weiboAccessTokenVo.getUid(), httpComponent);
            // 如果当前这个微博用户是第一次进入网站，自动注册
            SocialUserTo socialUserTo = oAuthService.get();
            socialUserTo.setExpireTime(Long.parseLong(weiboAccessTokenVo.getExpires_in()));
            R r = memberFeign.socialLogin(socialUserTo);
            if (r.isSuccess()) {
                // 登陆成功,返回首页
                MemberInfoBo memberInfoBo = r.get(MemberInfoBo.class);
                session.setAttribute(AuthConstant.LOGIN_USER_SESSION, memberInfoBo);// 将用户信息保存到session中
                /**
                 * 1. 解决session子域共享问题
                 *  参考：{@link SessionConfig#cookieSerializer()}
                 * 2. 使用json序列化方式将session数据存储到redis中
                 *  参考：{@link SessionConfig#redisSerializer()}
                 */
                log.info("微博用户【{}】登录成功", socialUserTo.getNickName());
                String sourceUrl = (String) session.getAttribute("source_url");
                session.removeAttribute("source_url");
                if (StringUtils.hasText(sourceUrl)) {
                    return "redirect:" + sourceUrl;
                }
                return "redirect:http://gulimall.com";
            } else {
                log.error("登录失败,返回登录页【请查看gulimall-member日志】");
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            log.error("获取微博accessToken失败,返回登录页");
            // 获取accessToken失败,重定向到登录页
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
