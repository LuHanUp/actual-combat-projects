package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.context.UserContext;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.response.UserProfileResult;
import top.luhancc.saas.hrm.common.model.system.type.UserLevelType;
import top.luhancc.saas.hrm.system.domain.param.LoginParam;
import top.luhancc.saas.hrm.system.domain.query.PermissionQuery;
import top.luhancc.saas.hrm.system.service.PermissionService;
import top.luhancc.saas.hrm.system.service.UserService;

import javax.servlet.http.HttpServletRequest;

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
    private final PermissionService permissionService;

    /**
     * 用户登录
     * <p>
     * 手机号密码登录
     *
     * @param loginParam 登录参数
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<String> login(@RequestBody LoginParam loginParam) {
        String token = userService.login(loginParam);
        if (StringUtils.isEmpty(token)) {
            return Result.error(ResultCode.LOGIN_ERROR);
        }
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
        User user = userService.findById(UserContext.getUserId());
        UserProfileResult profileResult = null;
        // 根据不同的用户级别获取对应的权限
        if (UserLevelType.USER.equals(user.getLevel())) {
            profileResult = new UserProfileResult(user);
        } else {
            PermissionQuery permissionQuery = new PermissionQuery();
            permissionQuery.setPage(1);
            permissionQuery.setSize(permissionService.count());
            if (UserLevelType.CO_ADMIN.equals(user.getLevel())) {
                permissionQuery.setEnVisible(1);
            }
            Page<Permission> permissionPage = permissionService.findAll(permissionQuery);
            profileResult = new UserProfileResult(user, permissionPage.getContent());
        }
        return Result.success(profileResult);
    }
}
