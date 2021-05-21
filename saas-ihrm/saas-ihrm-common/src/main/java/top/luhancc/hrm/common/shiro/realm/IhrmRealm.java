
package top.luhancc.hrm.common.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import top.luhancc.hrm.common.context.UserContext;
import top.luhancc.saas.hrm.common.model.system.bo.UserToken;
import top.luhancc.saas.hrm.common.model.system.response.UserProfileResult;

import java.util.Set;

//公共的realm：获取安全数据，构造权限信息
public class IhrmRealm extends AuthorizingRealm {

    @Override
    public void setName(String name) {
        super.setName("ihrmRealm");
    }

    //授权方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取安全数据
        UserProfileResult result = (UserProfileResult) principalCollection.getPrimaryPrincipal();
        //2.获取权限信息
        Set<String> apisPerms = (Set<String>) result.getRoles().get("apis");
        //3.构造权限数据，返回值
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(apisPerms);
        UserToken userToken = new UserToken();
        userToken.setId(result.getUserId());
        userToken.setMobile(result.getMobile());
        userToken.setUsername(result.getUsername());
        userToken.setCompanyId(result.getCompanyId());
        userToken.setCompanyName(result.getCompany());
        userToken.setLevel(result.getLevel());
        UserContext.setCurrentUser(userToken);
        return info;
    }

    //认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
