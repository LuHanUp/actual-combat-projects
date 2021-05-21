package top.luhancc.hrm.common.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.luhancc.hrm.common.context.UserContext;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.saas.hrm.common.model.system.bo.UserToken;
import top.luhancc.saas.hrm.common.model.system.response.UserProfileResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shiro拦截器,设置UserToken
 *
 * @author luHan
 * @create 2021/5/17 11:32
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Slf4j
public class ShiroInterceptor extends HandlerInterceptorAdapter {
    private final RedisSessionDAO redisSessionDAO;

    /**
     * 存放token的请求头名称
     */
    private final static String TOKEN_NAME = "Authorization";

    /**
     * 主要做的几件事情：
     * 1. 统一的用户权限验证,是否登录
     * 2. 判断用户是否具有访问当前接口的权限
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String authorization = request.getHeader(TOKEN_NAME);
        if (StringUtils.isEmpty(authorization)) {
            log.warn("没有携带请求头Authorization");
            throw new BaseBusinessException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        try {
            Session session = redisSessionDAO.readSession(token);
            SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
            UserProfileResult userProfileResult = (UserProfileResult) simplePrincipalCollection.getPrimaryPrincipal();
            UserToken userToken = new UserToken();
            userToken.setId(userProfileResult.getUserId());
            userToken.setMobile(userProfileResult.getMobile());
            userToken.setUsername(userProfileResult.getUsername());
            userToken.setCompanyId(userProfileResult.getCompanyId());
            userToken.setCompanyName(userProfileResult.getCompany());
            userToken.setLevel(userProfileResult.getLevel());
            UserContext.setCurrentUser(userToken);
        } catch (UnknownSessionException e) {
            log.warn("token不正确,无法获取认证信息:{}", token);
            throw new BaseBusinessException(ResultCode.UNAUTHENTICATED);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
