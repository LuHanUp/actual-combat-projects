package top.luhancc.gulimall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.luhancc.gulimall.product.config.properties.ThreadPoolProperties;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author luHan
 * @create 2021/1/11 11:57
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({ThreadPoolProperties.class})
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor poolExecutor(ThreadPoolProperties threadPoolProperties) {
        return new org.apache.tomcat.util.threads.ThreadPoolExecutor(
                threadPoolProperties.getCoreSize(), threadPoolProperties.getMaxSize(),
                threadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
