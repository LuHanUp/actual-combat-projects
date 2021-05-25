package top.luhancc.ihrm.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录过滤器
 * <p>
 * 因为鉴权写在了saas-ihrm-common，随意此处的这个可以不需要了
 *
 * @author luHan
 * @create 2021/5/25 15:25
 * @since 1.0.0
 */
//@Component
@Slf4j
public class LoginFilter extends ZuulFilter {

    /**
     * 定义过滤器类型
     * <pre>
     * pre：在执行路由请求之前执行此过滤器
     * routing：在路由请求时执行此过滤器
     * post：在routing和error之后执行
     * error：在请求出现异常后执行此过滤器
     * </pre>
     *
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 定义过滤器优先级，返回的数字越小，优先级越高
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 判断此过滤器是否需要执行
     *
     * @return true-执行 false-不执行
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 执行过滤器处理
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            log.warn("没有携带请求头Authorization");
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
