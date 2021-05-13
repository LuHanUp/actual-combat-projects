package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.PageResult;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.system.domain.query.UserQuery;
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

    @RequestMapping("/findAllByQuery")
    public Result<PageResult<User>> findAll(UserQuery userQuery) {
        userQuery.setCompanyId(companyId);
        Page<User> userPage = service.findAll(userQuery);
        return Result.success(new PageResult<>(userPage));
    }
}
