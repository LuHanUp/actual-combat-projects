package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.utils.JwtUtils;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.system.domain.param.LoginParam;
import top.luhancc.saas.hrm.system.service.UserService;

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
}
