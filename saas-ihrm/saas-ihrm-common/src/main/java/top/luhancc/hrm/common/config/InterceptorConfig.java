package top.luhancc.hrm.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.luhancc.hrm.common.interceptor.JwtInterceptor;
import top.luhancc.hrm.common.utils.JwtUtils;

/**
 * @author luHan
 * @create 2021/5/17 12:11
 * @since 1.0.0
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(jwtUtils)).addPathPatterns("/**")
                .excludePathPatterns("/sys/login", "/actuator/info", "/error/**");
    }
}
