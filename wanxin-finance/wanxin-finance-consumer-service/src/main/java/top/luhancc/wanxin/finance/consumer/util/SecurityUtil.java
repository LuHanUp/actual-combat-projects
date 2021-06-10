package top.luhancc.wanxin.finance.consumer.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.luhancc.wanxin.finance.common.domain.model.LoginUser;

import javax.servlet.http.HttpServletRequest;

public final class SecurityUtil {

    /**
     * 获取当前登录用户
     */
    public static LoginUser getUser() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();

            Object jwt = request.getAttribute("jsonToken");
            if (jwt instanceof LoginUser) {
                return (LoginUser) jwt;
            }
        }

        return new LoginUser();
    }

}
