package top.luhancc.saas.hrm.system.service;

import org.springframework.data.domain.Page;
import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.system.domain.param.AssignRoleParam;
import top.luhancc.saas.hrm.system.domain.query.UserQuery;

/**
 * @author luHan
 * @create 2021/5/13 15:35
 * @since 1.0.0
 */
public interface UserService extends CRUDService<User> {
    public Page<User> findAll(String companyId, UserQuery userQuery);

    /**
     * 分配角色
     *
     * @param assignRoleParam
     */
    void assignRoles(AssignRoleParam assignRoleParam);
}
