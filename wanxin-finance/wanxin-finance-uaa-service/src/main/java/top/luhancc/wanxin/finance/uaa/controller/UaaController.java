package top.luhancc.wanxin.finance.uaa.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.luhancc.wanxin.finance.uaa.common.utils.WebUtils;

import java.util.Map;


/**
 * @author luhan
 */
@Controller
public class UaaController {

    private static final Logger LOG = LoggerFactory.getLogger(UaaController.class);

    @GetMapping(value = {"/login"})
    public String login(Model model) {
        LOG.info("Go to login, IP: {}", WebUtils.getIp());
        return "login";
    }

    @RequestMapping("/confirm_access")
    public String confirmAccess() {
        return "/oauth_approval";
    }

    @RequestMapping("/oauth_error")
    public String oauthError() {
        return "/oauth_error";
    }


    @Autowired
    private AuthorizationServerTokenServices tokenService;

    @Autowired
    private AccessTokenConverter accessTokenConverter;

    /**
     * 认证请求是SpringSecurity OAuth框架提供好的一个请求，写死就是/oauth/token
     * <p>
     * 源码地址是：{@link TokenEndpoint#postAccessToken(java.security.Principal, java.util.Map)}
     * 请求参数参考：{@link TokenRequest}
     * 返回值参考：{@link OAuth2AccessToken}
     *      access_token：访问令牌
     *      token_type：令牌类型，传递令牌时需要在令牌前面添加这个类型作为前缀进行传递
     *      refresh_token：刷新令牌，访问令牌到期后可以通过刷新令牌重新生成访问令牌
     *      expires_in：访问令牌的有效期，单位是秒
     *      jti：身份令牌，主要是用来作为一次性token，从而回避重复请求攻击
     */
    // 源码如下：
    /*@RequestMapping(
            value = {"/oauth/token"},
            method = {RequestMethod.POST}
    )
    public ResponseEntity<OAuth2AccessToken> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) {
        return null;
    }*/

    /**
     * 解析令牌
     * <p>
     * SpringSecurity也提供了一个默认的实现，这里是覆盖了默认实现
     * <p>
     * 源码地址：{@link CheckTokenEndpoint#checkToken(java.lang.String)}
     * <p>
     * 源码：
     * <pre>{@code
     * @RequestMapping({"/oauth/check_token"})
     *     @ResponseBody
     *     public Map<String, ?> checkToken(@RequestParam("token") String value) {
     *         OAuth2AccessToken token = this.resourceServerTokenServices.readAccessToken(value);
     *         if (token == null) {
     *             throw new InvalidTokenException("Token was not recognised");
     *         } else if (token.isExpired()) {
     *             throw new InvalidTokenException("Token has expired");
     *         } else {
     *             OAuth2Authentication authentication = this.resourceServerTokenServices.loadAuthentication(token.getValue());
     *             return this.accessTokenConverter.convertAccessToken(token, authentication);
     *         }
     *     }
     * }
     * </pre>
     *
     * @param value
     * @return
     */
    @RequestMapping(value = "/oauth/check_token", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ?> checkToken(@RequestParam("token") String value) {
        DefaultTokenServices tokenServices = (DefaultTokenServices) tokenService;

        OAuth2AccessToken token = tokenServices.readAccessToken(value);
        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }

        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }
        OAuth2Authentication authentication = tokenServices.loadAuthentication(token.getValue());
        Map<String, ?> rst = accessTokenConverter.convertAccessToken(token, authentication);
        return rst;
    }

}
