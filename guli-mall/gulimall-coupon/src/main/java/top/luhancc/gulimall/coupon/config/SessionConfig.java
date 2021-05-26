package top.luhancc.gulimall.coupon.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * session配置
 *
 * @author luHan
 * @create 2021/1/12 15:04
 * @since 1.0.0
 */
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com");// 设置域名作用域
        cookieSerializer.setCookieName("GULISESSION");// 设置cookie名称
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }
}
