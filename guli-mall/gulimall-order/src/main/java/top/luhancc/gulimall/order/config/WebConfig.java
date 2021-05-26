package top.luhancc.gulimall.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.luhancc.gulimall.order.interceptor.CartInterceptor;
import top.luhancc.gulimall.order.interceptor.LoginUserInterceptor;

/**
 * @author luHan
 * @create 2021/1/13 11:03
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/cart/**");
        registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/web/order/**");
    }
}
