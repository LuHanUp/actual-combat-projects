package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.PageResult;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.system.domain.query.RoleQuery;
import top.luhancc.saas.hrm.system.service.RoleService;

/**
 * 角色controller
 *
 * @author luHan
 * @create 2021/5/14 11:45
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys/role")
public class RoleController extends BaseController<Role, RoleService> {

    @RequestMapping(value = "/findByPage", method = RequestMethod.GET)
    public Result<PageResult<Role>> findByPage(RoleQuery query) {
        Page<Role> rolePage = service.findByPage(companyId, query);
        return Result.success(new PageResult<>(rolePage));
    }
}
