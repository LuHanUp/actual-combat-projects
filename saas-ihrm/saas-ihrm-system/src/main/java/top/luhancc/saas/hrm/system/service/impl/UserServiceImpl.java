package top.luhancc.saas.hrm.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.system.dao.RoleDao;
import top.luhancc.saas.hrm.system.dao.UserDao;
import top.luhancc.saas.hrm.system.domain.param.AssignRoleParam;
import top.luhancc.saas.hrm.system.domain.query.UserQuery;
import top.luhancc.saas.hrm.system.service.UserService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author luHan
 * @create 2021/5/13 15:35
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService<User> implements UserService {
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final IdWorker idWorker;

    @Override
    public void save(User user) {
        //设置主键的值
        String id = idWorker.nextId() + "";
        user.setLevel("user");
        user.setPassword("123456");//设置初始密码
        user.setEnableState(1);
        user.setId(id);
        //调用dao保存部门
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        //1.根据id查询部门
        User target = userDao.findById(user.getId()).get();
        //2.设置部门属性
        target.setUsername(user.getUsername());
        target.setPassword(user.getPassword());
        target.setDepartmentId(user.getDepartmentId());
        target.setDepartmentName(user.getDepartmentName());
        //3.更新部门
        userDao.save(target);
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    @Override
    public List<User> findAll(String companyId) {
        return userDao.findAll(super.specByCompanyId(companyId));
    }

    @Override
    public Page<User> findAll(String companyId, UserQuery userQuery) {
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // 当公司id不为空时,添加公司id的条件
                if (!StringUtils.isEmpty(companyId)) {
                    predicates.add(criteriaBuilder.equal(root.get("companyId").as(String.class), companyId));
                }
                // 当部门id不为空时,添加部门id的条件
                if (!StringUtils.isEmpty(userQuery.getDepartmentId())) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), userQuery.getDepartmentId()));
                }
                // 当是否分配部门条件不能null时,追加筛选是否分配部门的条件
                if (userQuery.getHasDept() != null) {
                    if (0 == userQuery.getHasDept()) {
                        predicates.add(criteriaBuilder.isNull(root.get("departmentId")));
                    } else if (1 == userQuery.getHasDept()) {
                        predicates.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                    }
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        // 因为spring jpa的分页页码是从0开始的  所以这里需要将前端传递过来的页码 - 1
        return userDao.findAll(specification, PageRequest.of(userQuery.getPage() - 1, userQuery.getSize()));
    }

    @Override
    public void assignRoles(AssignRoleParam assignRoleParam) {
        User user = userDao.findById(assignRoleParam.getUserId()).get();
        // 设置用户的角色集合
        Set<Role> roles = new HashSet<>();
        for (String roleId : assignRoleParam.getRoleIds()) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        user.setRoles(roles);
        // 更新用户,因为给用户设置了roles jpa会自动维护用户和角色之间的关系
        userDao.save(user);
    }

    @Override
    public void deleteById(String id) {
        userDao.deleteById(id);
    }
}
