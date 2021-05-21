package top.luhancc.saas.hrm.common.model.system.response;

import lombok.Data;
import org.crazycake.shiro.AuthCachePrincipal;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.common.model.system.User;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户轮廓信息
 *
 * @author luHan
 * @create 2021/5/17 10:00
 * @since 1.0.0
 */
@Data
public class UserProfileResult implements Serializable, AuthCachePrincipal {
    private String mobile;
    private String userId;
    private String level;
    private String username;
    private String companyId;
    private String company;

    // 包含menus、points、apis
    private Map<String, Object> roles;

    public UserProfileResult(User user) {
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.company = user.getCompanyName();
        this.level = user.getLevel();
        this.userId = user.getId();
        this.companyId = user.getCompanyId();
        this.roles = new HashMap<>();
        Set<Role> roles = user.getRoles();
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for (Role role : roles) {
            Set<Permission> permissions = role.getPermissions();

            // 获取menu类型的权限code
            menus.addAll(permissions.stream()
                    .filter(permission -> permission.getType() == 1)
                    .map(Permission::getCode)
                    .collect(Collectors.toSet()));
            // 获取point类型的权限code
            points.addAll(permissions.stream()
                    .filter(permission -> permission.getType() == 2)
                    .map(Permission::getCode)
                    .collect(Collectors.toSet()));
            // 获取api类型的权限code
            apis.addAll(permissions.stream()
                    .filter(permission -> permission.getType() == 3)
                    .map(Permission::getCode)
                    .collect(Collectors.toSet()));
        }
        this.roles.put("menus", menus);
        this.roles.put("points", points);
        this.roles.put("apis", apis);
    }

    public UserProfileResult(User user, List<Permission> permissions) {
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.company = user.getCompanyName();
        this.level = user.getLevel();
        this.userId = user.getId();
        this.companyId = user.getCompanyId();
        this.roles = new HashMap<>();
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        // 获取menu类型的权限code
        menus.addAll(permissions.stream()
                .filter(permission -> permission.getType() == 1)
                .map(Permission::getCode)
                .collect(Collectors.toSet()));
        // 获取point类型的权限code
        points.addAll(permissions.stream()
                .filter(permission -> permission.getType() == 2)
                .map(Permission::getCode)
                .collect(Collectors.toSet()));
        // 获取api类型的权限code
        apis.addAll(permissions.stream()
                .filter(permission -> permission.getType() == 3)
                .map(Permission::getCode)
                .collect(Collectors.toSet()));
        this.roles.put("menus", menus);
        this.roles.put("points", points);
        this.roles.put("apis", apis);
    }

    @Override
    public String getAuthCacheKey() {
        return null;
    }
}
