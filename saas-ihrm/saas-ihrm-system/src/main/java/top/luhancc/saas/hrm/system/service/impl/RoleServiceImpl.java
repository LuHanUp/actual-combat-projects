package top.luhancc.saas.hrm.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.system.dao.RoleDao;
import top.luhancc.saas.hrm.system.domain.query.RoleQuery;
import top.luhancc.saas.hrm.system.service.RoleService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/14 11:46
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseService<Role> implements RoleService {
    private final RoleDao roleDao;
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
}
