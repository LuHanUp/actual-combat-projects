package top.luhancc.wanxin.finance.third.service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.luhancc.wanxin.finance.sms.common.cache.Cache;
import top.luhancc.wanxin.finance.sms.common.cache.RedisCache;


@Configuration
public class RedisConfig {
	
	@Bean
	public Cache cache(StringRedisTemplate redisTemplate){
		return new RedisCache(redisTemplate);
	}
	

}
