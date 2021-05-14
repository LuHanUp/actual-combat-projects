package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.PageResult;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.response.UserResult;
import top.luhancc.saas.hrm.system.domain.param.AssignRoleParam;
import top.luhancc.saas.hrm.system.domain.query.UserQuery;
import top.luhancc.saas.hrm.system.mapping.UserMapping;
import top.luhancc.saas.hrm.system.service.UserService;

/**
 * @author luHan
 * @create 2021/5/13 16:35
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys/user")
public class UserController extends BaseController<User, UserService> {
    private final UserMapping userMapping;

    @RequestMapping(value = "/findAllByQuery", method = RequestMethod.GET)
    public Result<PageResult<User>> findAll(UserQuery userQuery) {
        Page<User> userPage = service.findAll(companyId, userQuery);
        return Result.success(new PageResult<>(userPage));
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result<UserResult> findById(@PathVariable("id") String id) {
        User user = service.findById(id);
        return Result.success(new UserResult(user));
    }

    /**
     * 分配角色
     *
     * @return
     */
    @RequestMapping(value = "/assignRoles", method = RequestMethod.PUT)
    public Result<Void> assignRoles(@RequestBody AssignRoleParam assignRoleParam) {
        service.assignRoles(assignRoleParam);
        return Result.success();
    }
}
