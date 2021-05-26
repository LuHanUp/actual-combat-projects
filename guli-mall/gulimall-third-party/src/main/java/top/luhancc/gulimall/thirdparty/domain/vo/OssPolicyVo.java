package top.luhancc.gulimall.thirdparty.domain.vo;

import lombok.Data;

/**
 * @author luHan
 * @create 2020/12/10 14:22
 * @since 1.0.0
 */
@Data
public class OssPolicyVo {
    private String accessId;
    private String policy;
    private String signature;
    private String dir;
    private String host;
    private String expire;
}
