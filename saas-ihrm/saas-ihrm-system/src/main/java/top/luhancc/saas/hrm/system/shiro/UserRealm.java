package top.luhancc.saas.hrm.system.shiro;

import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import top.luhancc.hrm.common.shiro.realm.IhrmRealm;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.response.UserProfileResult;
import top.luhancc.saas.hrm.system.domain.query.PermissionQuery;
import top.luhancc.saas.hrm.system.service.PermissionService;
import top.luhancc.saas.hrm.system.service.UserService;

/**
 * 用户登录的Realm类
 *
 * @author luhan
 */
@Component
@Order(1)
public class UserRealm extends IhrmRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    //认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.获取用户的手机号和密码
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String mobile = upToken.getUsername();
        String password = new String(upToken.getPassword());
        //2.根据手机号查询用户
        User user = userService.findByMobile(mobile);
        //3.判断用户是否存在，用户密码是否和输入密码一致
        if (user != null && user.getPassword().equals(password)) {
            //4.构造安全数据并返回（安全数据：用户基本数据，权限信息 profileResult）
            UserProfileResult result = null;
            if ("user".equals(user.getLevel())) {
                result = new UserProfileResult(user);
            } else {
                PermissionQuery permissionQuery = new PermissionQuery();
                permissionQuery.setPage(1);
                permissionQuery.setSize(permissionService.count());
                if ("coAdmin".equals(user.getLevel())) {
                    permissionQuery.setEnVisible(1);
                }
                Page<Permission> permissionPage = permissionService.findAll(permissionQuery);
                result = new UserProfileResult(user, permissionPage.getContent());
            }
            //构造方法：安全数据，密码，realm域名
            return new SimpleAuthenticationInfo(result, user.getPassword(), this.getName());
        }
        //返回null，会抛出异常，标识用户名和密码不匹配
        return null;
    }
}
