package top.luhancc.saas.hrm.system.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.utils.JwtUtils;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.response.UserProfileResult;
import top.luhancc.saas.hrm.system.domain.param.LoginParam;
import top.luhancc.saas.hrm.system.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luHan
 * @create 2021/5/14 17:17
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys")
@Slf4j
public class SystemController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     *
     * @param loginParam 登录参数
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<String> login(@RequestBody LoginParam loginParam) {
        User user = userService.findByMobile(loginParam.getMobile());
        if (user == null || !user.getPassword().equals(loginParam.getPassword())) {
            return Result.error(ResultCode.LOGIN_ERROR);
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("companyId", user.getCompanyId());
        map.put("companyName", user.getCompanyName());
        String token = jwtUtils.createJwt(user.getId(), user.getUsername(), map);
        return Result.success(token);
    }

    /**
     * 获取当前登录用户的信息
     * <p>
     * 请求头携带token的规则：Authorization:Bearer token字符串
     *
     * @return
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result<UserProfileResult> profile(HttpServletRequest request) {
        // 从请求头中获取token
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            log.warn("没有携带请求头Authorization");
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        Claims claims = jwtUtils.parseJwt(token);
        if (claims == null) {
            log.warn("token不正确,无法获取认证信息:{}", token);
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        // 根据token获取其中的用户id
        String userId = claims.getId();
        User user = userService.findById(userId);
        return Result.success(new UserProfileResult(user));
    }
}
