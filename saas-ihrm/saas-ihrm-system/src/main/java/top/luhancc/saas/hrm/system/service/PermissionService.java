package top.luhancc.saas.hrm.system.service;

import org.springframework.data.domain.Page;
import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.system.domain.param.PermissionParam;
import top.luhancc.saas.hrm.system.domain.query.PermissionQuery;

/**
 * 权限service接口
 *
 * @author luHan
 * @create 2021/5/14 10:22
 * @since 1.0.0
 */
public interface PermissionService extends CRUDService<PermissionParam> {
    Page<Permission> findAll(String companyId, PermissionQuery permissionQuery);
}
