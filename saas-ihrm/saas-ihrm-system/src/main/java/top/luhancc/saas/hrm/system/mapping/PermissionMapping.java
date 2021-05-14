package top.luhancc.saas.hrm.system.mapping;

import org.mapstruct.Mapper;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.PermissionApi;
import top.luhancc.saas.hrm.common.model.system.PermissionMenu;
import top.luhancc.saas.hrm.common.model.system.PermissionPoint;
import top.luhancc.saas.hrm.system.domain.param.PermissionParam;

/**
 * @author luHan
 * @create 2021/5/14 10:44
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface PermissionMapping {
    Permission param2Permission(PermissionParam permissionParam);

    PermissionMenu param2PermissionMenu(PermissionParam permissionParam);

    PermissionPoint param2PermissionPoint(PermissionParam permissionParam);

    PermissionApi param2PermissionApi(PermissionParam permissionParam);

    PermissionParam permission2Param(Permission permission);
}
