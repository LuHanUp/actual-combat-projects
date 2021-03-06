package top.luhancc.saas.hrm.system.service;

import org.springframework.data.domain.Page;
import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.system.domain.param.AssignPermParam;
import top.luhancc.saas.hrm.system.domain.query.RoleQuery;

/**
 * @author luHan
 * @create 2021/5/14 11:46
 * @since 1.0.0
 */
public interface RoleService extends CRUDService<Role> {
    Page<Role> findByPage(String companyId, RoleQuery query);

    /**
     * 给角色分配权限
     *
     * @param assignPermParam 分配角色的参数
     */
    void assignPerms(AssignPermParam assignPermParam);
}
