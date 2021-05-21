package top.luhancc.saas.hrm.system.service.impl;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.hrm.common.utils.JwtUtils;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.bo.UserToken;
import top.luhancc.saas.hrm.common.model.system.type.PermissionType;
import top.luhancc.saas.hrm.system.dao.RoleDao;
import top.luhancc.saas.hrm.system.dao.UserDao;
import top.luhancc.saas.hrm.system.domain.param.AssignRoleParam;
import top.luhancc.saas.hrm.system.domain.param.LoginParam;
import top.luhancc.saas.hrm.system.domain.query.UserQuery;
import top.luhancc.saas.hrm.system.mapping.UserMapping;
import top.luhancc.saas.hrm.system.service.UserService;
import top.luhancc.saas.hrm.system.thirdservice.FaceService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/5/13 15:35
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends BaseService<User> implements UserService {
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final IdWorker idWorker;
    private final JwtUtils jwtUtils;
    private final UserMapping userMapping;
    @Autowired
    @Qualifier("baidu-face")
    private FaceService faceService;

    @Override
    public void save(User user) {
        //设置主键的值
        String id = idWorker.nextId() + "";
        user.setLevel("user");
        user.setPassword(new Md5Hash("123456", user.getMobile(), 3).toString());//设置初始密码
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
    public User findByMobile(String mobile) {
        return userDao.findByMobile(mobile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadStaffPhoto(String userId, MultipartFile file) {
        User user = userDao.findById(userId).get();
        // 对图片进行Base64编码
        String imgBase64Str = null;
        try {
            imgBase64Str = Base64.encode(file.getBytes());
            // 更新用户头像
            user.setStaffPhoto(imgBase64Str);
            userDao.save(user);

            boolean faceExists = faceService.faceExists(userId);
            boolean faceUpdate = true;
            if (faceExists) {
                // 更新人脸库的头像
                faceUpdate = faceService.faceUpdate(userId, imgBase64Str);
            } else {
                // 将头像注册进人脸库中
                faceUpdate = faceService.faceRegister(userId, imgBase64Str);
            }
            if (!faceUpdate) {
                throw new BaseBusinessException(ResultCode.USER_HEAD_IMG_ERROR);
            }
            return String.format("data:image/png;base64,%s", imgBase64Str);
        } catch (IOException e) {
            log.error("图片文件编码失败:" + e);
        }
        return null;
    }

    @Override
    public String login(LoginParam loginParam) {
        User user = this.findByMobile(loginParam.getMobile());
        String password = loginParam.getPassword();
        password = new Md5Hash(password, loginParam.getMobile(), 3).toString();
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        UserToken userToken = userMapping.user2UserToken(user);
        Map<String, Object> map = new HashMap<>(2);
        map.put("companyId", user.getCompanyId());
        map.put("companyName", user.getCompanyName());
        map.put("user", userToken);

        // 获取到当前用户可以访问的所有api权限
        Set<String> apiCodes = new HashSet<>();
        for (Role role : user.getRoles()) {
            Set<Permission> permissions = role.getPermissions();
            apiCodes.addAll(permissions.stream()
                    .filter(permission -> permission.getType() == PermissionType.API)
                    .map(Permission::getCode)
                    .collect(Collectors.toSet()));
        }
        map.put("apiCodes", apiCodes);
        return jwtUtils.createJwt(user.getId(), user.getUsername(), map);
    }

    @Override
    public String loginByShiro(LoginParam loginParam) {
        String password = loginParam.getPassword();
        password = new Md5Hash(password, loginParam.getMobile(), 3).toString();
        UsernamePasswordToken upToken = new UsernamePasswordToken(loginParam.getMobile(), password);
        Subject subject = SecurityUtils.getSubject();
        subject.login(upToken);
        return subject.getSession().getId().toString();
    }

    @Override
    public void deleteById(String id) {
        userDao.deleteById(id);
    }
}
