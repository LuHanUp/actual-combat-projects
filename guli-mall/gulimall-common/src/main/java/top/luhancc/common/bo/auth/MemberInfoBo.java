package top.luhancc.common.bo.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员用户信息
 *
 * @author luHan
 * @create 2021/1/12 14:08
 * @since 1.0.0
 */
@Data
public class MemberInfoBo implements Serializable {
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 加密盐
     */
//    private String salt;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 社交账号
     */
    private String account;

    /**
     * 社交账户的token
     */
    private String accessToken;

    /**
     * 社交账号过期时间
     */
    private Long expireTime;

}
