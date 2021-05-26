package top.luhancc.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.luhancc.common.interceotors.UnifyErrorHandlerInterceptor;
import top.luhancc.common.interceotors.UnifyResultHandlerInterceptor;

/**
 * @author luHan
 * @create 2021/1/26 15:31
 * @since 1.0.0
 */
@Configuration
@Import({UnifyErrorHandlerInterceptor.class, UnifyResultHandlerInterceptor.class})
public class CommonWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new UnifyResultHandlerInterceptor())
//                .excludePathPatterns("/**/*.js")
//                .excludePathPatterns("/**/*.css")
//                .excludePathPatterns("/**/*.html");
    }
}
