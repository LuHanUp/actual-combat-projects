package top.luhancc.gulimall.auth.domain.vo;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/26 11:20
 * @since 1.0.0
 */
@Data
public class MemberRegisterVo {
    private String userName;
    private String password;
    private String phone;
}
