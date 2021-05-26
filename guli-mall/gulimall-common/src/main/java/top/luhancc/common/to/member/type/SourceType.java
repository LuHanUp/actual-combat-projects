package top.luhancc.common.to.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户注册来源
 *
 * @author luHan
 * @create 2021/1/12 10:37
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SourceType {
    REGISTER(1, "直接注册"),
    WEIBO(2, "通过微博注册"),
    ;

    private Integer type;
    private String desc;
}
