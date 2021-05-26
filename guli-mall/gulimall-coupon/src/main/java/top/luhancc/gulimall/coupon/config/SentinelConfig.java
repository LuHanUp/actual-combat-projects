package top.luhancc.gulimall.coupon.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import top.luhancc.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author luHan
 * @create 2021/1/21 14:19
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class SentinelConfig {

    @Bean
    @Primary
    public BlockExceptionHandler blockExceptionHandler() {
        return new DefaultBlockHandler();
    }

    public static class DefaultBlockHandler implements BlockExceptionHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
//            log.warn("被流控了:", e);
            // Return 429 (Too Many Requests) by default.
            response.setStatus(429);
            StringBuffer url = request.getRequestURL();
            if ("GET".equals(request.getMethod()) && StringUtil.isNotBlank(request.getQueryString())) {
                url.append("?").append(request.getQueryString());
            }
            R r = R.error(429, "被流控了:" + e.getLocalizedMessage());
            if (e instanceof FlowException) {
                FlowException fe = (FlowException) e;
                String resource = fe.getRule().getResource();
                r = R.error(429, "被流控了,路径为:" + resource);
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            PrintWriter out = response.getWriter();
            out.print(JSON.toJSONString(r));
            out.flush();
            out.close();
        }
    }
}
