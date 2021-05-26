package top.luhancc.gulimall.auth.domain.vo;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/11 16:10
 * @since 1.0.0
 */
@Data
public class RegisterVo {
    private String username;
    private String password;
    private String phone;
    private String code;
}
