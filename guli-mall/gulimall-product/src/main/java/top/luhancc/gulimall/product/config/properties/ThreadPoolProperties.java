package top.luhancc.gulimall.product.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池的配置参数类
 *
 * @author luHan
 * @create 2021/1/11 13:14
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class ThreadPoolProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
