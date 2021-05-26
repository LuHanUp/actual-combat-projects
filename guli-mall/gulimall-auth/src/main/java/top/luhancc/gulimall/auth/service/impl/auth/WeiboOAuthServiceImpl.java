package top.luhancc.gulimall.auth.service.impl.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import top.luhancc.common.component.HttpComponent;
import top.luhancc.common.to.member.SocialUserTo;
import top.luhancc.common.to.member.type.SourceType;
import top.luhancc.gulimall.auth.service.OAuthService;
import top.luhancc.gulimall.auth.service.domain.WeiboUserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 微博获取社交用户信息
 *
 * @author luHan
 * @create 2021/1/12 10:43
 * @since 1.0.0
 */
@AllArgsConstructor
@Slf4j
public class WeiboOAuthServiceImpl implements OAuthService {
    private final String accessToken;
    private final String uid;
    private final HttpComponent httpComponent;

    @Override
    public SocialUserTo get() {
        String url = "https://api.weibo.com/2/users/show.json";

        Map<String, String> uriVariable = new HashMap<>(2);
        uriVariable.put("access_token", accessToken);// access_token true string 采用OAuth授权方式为必填参数，OAuth授权后获得。
        uriVariable.put("uid", uid);// uid false int64 需要查询的用户ID。

        ResponseEntity<WeiboUserInfo> responseEntity = null;
        try {
            responseEntity = httpComponent.get(url, uriVariable, WeiboUserInfo.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                WeiboUserInfo weiboUserInfo = responseEntity.getBody();
                SocialUserTo socialUserTo = new SocialUserTo();
                socialUserTo.setAccount(weiboUserInfo.getId().toString());
                socialUserTo.setAccessToken(accessToken);
                socialUserTo.setNickName(weiboUserInfo.getScreenName());
                socialUserTo.setHeadImg(weiboUserInfo.getProfileImageUrl());
                socialUserTo.setSourceType(SourceType.WEIBO);
                socialUserTo.setGender("m".equals(weiboUserInfo.getGender()) ? 1 : 0);
                return socialUserTo;
            } else {
                log.error("获取微博用户信息失败:{}", responseEntity);
                throw new RuntimeException("获取微博用户信息失败");
            }
        } catch (RestClientException e) {
            log.error("请求'{}'失败:", url, e);
            throw new RuntimeException("获取微博用户信息失败");
        }
    }
}
