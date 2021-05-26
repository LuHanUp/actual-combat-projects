package top.luhancc.common.to.member;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/11 16:30
 * @since 1.0.0
 */
@Data
public class MemberLoginTo {
    /**
     * 用户名或手机号码
     */
    private String useracct;

    /**
     * 密码
     */
    private String password;
}
