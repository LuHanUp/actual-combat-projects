package top.luhancc.gulimall.order.domain.cat.to;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/13 10:54
 * @since 1.0.0
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    // cookie中是否有临时用户
    private Boolean tempUser = false;
}
