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
import top.luhancc.saas.hrm.system.domain.param.SocialLoginParam;
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
        //??????????????????
        String id = idWorker.nextId() + "";
        user.setLevel("user");
        user.setPassword(new Md5Hash("123456", user.getMobile(), 3).toString());//??????????????????
        user.setEnableState(1);
        user.setId(id);
        //??????dao????????????
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        //1.??????id????????????
        User target = userDao.findById(user.getId()).get();
        //2.??????????????????
        target.setUsername(user.getUsername());
        target.setPassword(user.getPassword());
        target.setDepartmentId(user.getDepartmentId());
        target.setDepartmentName(user.getDepartmentName());
        //3.????????????
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

                // ?????????id????????????,????????????id?????????
                if (!StringUtils.isEmpty(companyId)) {
                    predicates.add(criteriaBuilder.equal(root.get("companyId").as(String.class), companyId));
                }
                // ?????????id????????????,????????????id?????????
                if (!StringUtils.isEmpty(userQuery.getDepartmentId())) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), userQuery.getDepartmentId()));
                }
                // ?????????????????????????????????null???,???????????????????????????????????????
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
        // ??????spring jpa?????????????????????0?????????  ???????????????????????????????????????????????? - 1
        return userDao.findAll(specification, PageRequest.of(userQuery.getPage() - 1, userQuery.getSize()));
    }

    @Override
    public void assignRoles(AssignRoleParam assignRoleParam) {
        User user = userDao.findById(assignRoleParam.getUserId()).get();
        // ???????????????????????????
        Set<Role> roles = new HashSet<>();
        for (String roleId : assignRoleParam.getRoleIds()) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        user.setRoles(roles);
        // ????????????,????????????????????????roles jpa?????????????????????????????????????????????
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
        // ???????????????Base64??????
        String imgBase64Str = null;
        try {
            imgBase64Str = Base64.encode(file.getBytes());
            // ??????????????????
            user.setStaffPhoto(imgBase64Str);
            userDao.save(user);

            boolean faceExists = faceService.faceExists(userId);
            boolean faceUpdate = true;
            if (faceExists) {
                // ????????????????????????
                faceUpdate = faceService.faceUpdate(userId, imgBase64Str);
            } else {
                // ??????????????????????????????
                faceUpdate = faceService.faceRegister(userId, imgBase64Str);
            }
            if (!faceUpdate) {
                throw new BaseBusinessException(ResultCode.USER_HEAD_IMG_ERROR);
            }
            return String.format("data:image/png;base64,%s", imgBase64Str);
        } catch (IOException e) {
            log.error("????????????????????????:" + e);
        }
        return null;
    }

    @Override
    public String login(LoginParam loginParam, String authType) {
        if ("jwt".equals(authType) || StringUtils.isEmpty(authType)) {
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

            // ??????????????????????????????????????????api??????
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
        } else if ("shiro".equals(authType)) {
            String password = loginParam.getPassword();
            password = new Md5Hash(password, loginParam.getMobile(), 3).toString();
            UsernamePasswordToken upToken = new UsernamePasswordToken(loginParam.getMobile(), password);
            Subject subject = SecurityUtils.getSubject();
            subject.login(upToken);
            return subject.getSession().getId().toString();
        }
        return null;
    }

    @Override
    public String socialLogin(SocialLoginParam socialLoginParam, String authType) {
        User user = this.findByMobile(socialLoginParam.getMobile());
        if ("jwt".equals(authType) || StringUtils.isEmpty(authType)) {
            UserToken userToken = userMapping.user2UserToken(user);
            Map<String, Object> map = new HashMap<>(2);
            map.put("companyId", user.getCompanyId());
            map.put("companyName", user.getCompanyName());
            map.put("user", userToken);

            // ??????????????????????????????????????????api??????
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
        } else if ("shiro".equals(authType)) {
            String password = user.getPassword();
            password = new Md5Hash(password, socialLoginParam.getMobile(), 3).toString();
            UsernamePasswordToken upToken = new UsernamePasswordToken(socialLoginParam.getMobile(), password);
            Subject subject = SecurityUtils.getSubject();
            subject.login(upToken);
            return subject.getSession().getId().toString();
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        userDao.deleteById(id);
    }
}
