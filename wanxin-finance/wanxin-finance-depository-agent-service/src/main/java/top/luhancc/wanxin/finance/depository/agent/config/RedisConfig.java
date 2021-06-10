package top.luhancc.wanxin.finance.depository.agent.config;

import top.luhancc.wanxin.finance.common.cache.Cache;
import top.luhancc.wanxin.finance.depository.agent.common.cache.RedisCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;


@Configuration
public class RedisConfig {

    @Bean
    public Cache cache(StringRedisTemplate redisTemplate) {
        return new RedisCache(redisTemplate);
    }
}
