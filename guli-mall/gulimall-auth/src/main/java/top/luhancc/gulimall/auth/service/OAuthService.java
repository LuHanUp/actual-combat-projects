package top.luhancc.gulimall.auth.service;

import top.luhancc.common.to.member.SocialUserTo;

/**
 * @author luHan
 * @create 2021/1/12 10:42
 * @since 1.0.0
 */
public interface OAuthService {
    /**
     * 获取社交用户数据
     *
     * @return
     */
    SocialUserTo get();
}
