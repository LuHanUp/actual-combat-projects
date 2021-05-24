package top.luhancc.hrm.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.luhancc.hrm.common.interceptor.JwtInterceptor;
import top.luhancc.hrm.common.utils.JwtUtils;

/**
 * JWT认证授权的配置类
 *
 * @author luHan
 * @create 2021/5/17 12:11
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "authorization", name = "type", havingValue = "jwt", matchIfMissing = false)
@Slf4j
public class JwtAuthorizationConfig implements WebMvcConfigurer {
    public JwtAuthorizationConfig() {
        log.info("使用jwt作为认证授权的组件");
    }

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(jwtUtils)).addPathPatterns("/**")
                .excludePathPatterns("/sys/login", "/actuator/info", "/sys/faceLogin/**", "/error/**", "/sys/permission/save/apis");
    }
}
