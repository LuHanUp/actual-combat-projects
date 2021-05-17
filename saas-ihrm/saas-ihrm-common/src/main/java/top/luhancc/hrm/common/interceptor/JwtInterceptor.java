package top.luhancc.hrm.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.luhancc.hrm.common.context.UserContext;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.hrm.common.utils.JwtUtils;
import top.luhancc.saas.hrm.common.model.system.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Jwt拦截器,做鉴权处理
 *
 * @author luHan
 * @create 2021/5/17 11:32
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor extends HandlerInterceptorAdapter {
    private final JwtUtils jwtUtils;

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
        Claims claims = null;
        try {
            claims = jwtUtils.parseJwt(token);
        } catch (SignatureException e) {
            log.warn("token不正确,无法获取认证信息:{}", token);
            throw new BaseBusinessException(ResultCode.UNAUTHENTICATED);
        } catch (ExpiredJwtException e) {
            log.warn("token已过期,无法获取认证信息:{}", token);
            throw new BaseBusinessException(ResultCode.RELOGIN_ERROR);
        }
        // 根据token获取其中的用户
        Object userObj = claims.get("user");
        User user = JSONObject.parseObject(JSONObject.toJSONString(userObj), User.class);
        UserContext.setCurrentUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
