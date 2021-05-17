package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.PageResult;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.system.domain.param.PermissionParam;
import top.luhancc.saas.hrm.system.domain.query.PermissionQuery;
import top.luhancc.saas.hrm.system.service.PermissionService;

/**
 * 权限controller
 *
 * @author luHan
 * @create 2021/5/14 10:21
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys/permission")
public class PermissionController extends BaseController<PermissionParam, PermissionService> {

    @RequestMapping("/findAllByQuery")
    public Result<PageResult<Permission>> findAll(PermissionQuery permissionQuery) {
        Page<Permission> permissionPage = service.findAll(permissionQuery);
        return Result.success(new PageResult<>(permissionPage));
    }
}
