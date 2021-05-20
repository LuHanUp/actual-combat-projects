package top.luhancc.saas.hrm.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.system.dao.PermissionDao;
import top.luhancc.saas.hrm.system.dao.RoleDao;
import top.luhancc.saas.hrm.system.domain.param.AssignPermParam;
import top.luhancc.saas.hrm.system.domain.query.RoleQuery;
import top.luhancc.saas.hrm.common.model.system.type.PermissionType;
import top.luhancc.saas.hrm.system.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author luHan
 * @create 2021/5/14 11:46
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseService<Role> implements RoleService {
    private final RoleDao roleDao;
    private final PermissionDao permissionDao;
    private final IdWorker idWorker;

    @Override
    public void save(Role role) {
        //填充其他参数
        role.setId(idWorker.nextId() + "");
        roleDao.save(role);
    }

    @Override
    public void update(Role role) {
        Role target = roleDao.getOne(role.getId());
        target.setDescription(role.getDescription());
        target.setName(role.getName());
        roleDao.save(target);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id).get();
    }

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(specByCompanyId(companyId));
    }

    @Override
    public void deleteById(String id) {
        roleDao.deleteById(id);
    }

    @Override
    public Page<Role> findByPage(String companyId, RoleQuery query) {
        return roleDao.findAll(specByCompanyId(companyId), PageRequest.of(query.getPage() - 1, query.getSize()));
    }

    @Override
    public void assignPerms(AssignPermParam assignPermParam) {
        Role role = roleDao.findById(assignPermParam.getRoleId()).get();
        Set<Permission> perms = new HashSet<>();
        for (String permsId : assignPermParam.getPermsIds()) {
            Permission permission = permissionDao.findById(permsId).get();
            // 根据父id和类型查询权限列表
            List<Permission> apiPermissions = permissionDao.findByTypeAndPid(PermissionType.API, permission.getId());
            perms.add(permission);
            perms.addAll(apiPermissions);// 赋予api权限
        }
        role.setPermissions(perms);
        roleDao.save(role);
    }
}
