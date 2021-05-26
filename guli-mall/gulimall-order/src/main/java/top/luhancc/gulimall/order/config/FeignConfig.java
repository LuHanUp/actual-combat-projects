package top.luhancc.gulimall.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author luHan
 * @create 2021/1/14 15:15
 * @since 1.0.0
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        // 在使用Feign进行远程调用时,在Feign创建的新的request中追加上请求头信息
        RequestInterceptor appendHeader = (template) -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // 向Feign创建的request中追加header信息
                template.header(headerName, request.getHeader(headerName));
            }
        };
        return appendHeader;
    }
}
