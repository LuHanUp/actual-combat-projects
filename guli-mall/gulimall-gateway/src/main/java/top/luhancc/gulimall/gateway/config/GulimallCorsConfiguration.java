package top.luhancc.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 解决跨域的配置类
 *
 * @author luHan
 * @create 2020/12/8 19:48
 * @since 1.0.0
 */
@Configuration
public class GulimallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");// 允许哪些头可以跨域
        corsConfiguration.addAllowedMethod("*");// 允许哪些方法可以跨域
        corsConfiguration.addAllowedOrigin("*");// 允许哪些地址可以跨域
        corsConfiguration.setAllowCredentials(true);// 是否允许携带cookie的跨域请求

        // 设置地址的跨域规则
        configurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(configurationSource);
    }
}
