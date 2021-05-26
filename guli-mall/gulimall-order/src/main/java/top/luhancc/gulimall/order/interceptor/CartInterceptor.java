package top.luhancc.gulimall.order.interceptor;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.luhancc.common.bo.auth.MemberInfoBo;
import top.luhancc.common.constant.AuthConstant;
import top.luhancc.gulimall.order.domain.cat.to.UserInfoTo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


/**
 * 执行目标方法之前，判断用户登录状态，并封装传递给目标请求
 *
 * @author luHan
 * @create 2021/1/13 10:49
 * @since 1.0.0
 */
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

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
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberInfoBo user = (MemberInfoBo) session.getAttribute(AuthConstant.LOGIN_USER_SESSION);
        if (user != null) {// 用户登录了
            userInfoTo.setUserId(user.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (AuthConstant.USER_KEY_COOKIE.equals(name)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        if (StringUtils.isBlank(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 分配临时用户，让浏览器保存
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.getTempUser()) {
            Cookie cookie = new Cookie(AuthConstant.USER_KEY_COOKIE, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(AuthConstant.USER_KEY_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
