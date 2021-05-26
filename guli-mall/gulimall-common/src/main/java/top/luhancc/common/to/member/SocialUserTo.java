package top.luhancc.common.to.member;

import lombok.Data;
import top.luhancc.common.to.member.type.SourceType;

/**
 * 社交用户数据类
 *
 * @author luHan
 * @create 2021/1/12 10:18
 * @since 1.0.0
 */
@Data
public class SocialUserTo {
    /**
     * 社交账号的唯一标识
     * <p>
     * 微博：uid
     * QQ：
     */
    private String account;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 注册来源
     */
    private SourceType sourceType;

    /**
     * 用户性别 1:男 0:女
     */
    private Integer gender;
}
