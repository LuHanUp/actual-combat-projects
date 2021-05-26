package top.luhancc.gulimall.thirdparty.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author luHan
 * @create 2020/12/10 14:16
 * @since 1.0.0
 */
@Configuration
@RefreshScope
@Data
public class OssConfiguration {
    @Value("${spring.cloud.alicloud.oss.bucket-name}")
    private String bucketName;
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;
}
