package top.luhancc.gulimall.coupon.interceptor;

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
        HttpSession session = request.getSession();
        MemberInfoBo user = (MemberInfoBo) session.getAttribute(AuthConstant.LOGIN_USER_SESSION);
        if (user != null) {// 用户登录了
            loginUser.set(user);
            return true;
        } else {
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
