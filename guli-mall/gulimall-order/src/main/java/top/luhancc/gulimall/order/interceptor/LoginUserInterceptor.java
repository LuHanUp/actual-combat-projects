package top.luhancc.gulimall.order.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import top.luhancc.common.bo.auth.MemberInfoBo;
import top.luhancc.common.constant.AuthConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;


/**
 * 执行目标方法之前，判断用户登录状态，如果没有登录就返回登录页面
 *
 * @author luHan
 * @create 2021/1/13 10:49
 * @since 1.0.0
 */
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberInfoBo> loginUser = new ThreadLocal<>();

    /**
     * 目标方法执行之前会执行
     *
     * @param request
     * @param response
     * @param handler
     * @return true会放行继续执行目标方法 false拦截请求不会执行目标方法
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String requestUri = request.getRequestURI();
        boolean match = antPathMatcher.match("/order/order/status/**", requestUri);
        boolean match1 = antPathMatcher.match("/payed/notify", requestUri);
        if (match || match1) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberInfoBo user = (MemberInfoBo) session.getAttribute(AuthConstant.LOGIN_USER_SESSION);
        if (user != null) {// 用户登录了
            loginUser.set(user);
            return true;
        } else {
            String requestURI = requestUri;
            Enumeration<String> parameterNames = request.getParameterNames();
            if (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                String parameter = request.getParameter(parameterName);
                requestURI += "?" + parameterName + "=" + parameter;
                while (parameterNames.hasMoreElements()) {
                    parameterName = parameterNames.nextElement();
                    parameter = request.getParameter(parameterName);
                    requestURI += "&" + parameterName + "=" + parameter;
                }
            }
            session.setAttribute("source_url", "http://order.gulimall.com/" + requestURI);
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
