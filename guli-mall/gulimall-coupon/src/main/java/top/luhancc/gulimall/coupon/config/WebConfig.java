package top.luhancc.gulimall.coupon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.luhancc.gulimall.coupon.interceptor.LoginUserInterceptor;

/**
 * @author luHan
 * @create 2021/1/13 11:03
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/seckill/kill/**");
    }
}
