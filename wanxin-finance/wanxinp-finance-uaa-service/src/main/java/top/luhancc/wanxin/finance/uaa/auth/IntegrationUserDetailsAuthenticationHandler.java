package top.luhancc.wanxin.finance.uaa.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import top.luhancc.wanxin.finance.common.domain.ErrorCode;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountLoginDTO;
import top.luhancc.wanxin.finance.uaa.common.utils.ApplicationContextHelper;
import top.luhancc.wanxin.finance.uaa.domain.UnifiedUserDetails;
import top.luhancc.wanxin.finance.uaa.feign.AccountFeign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证处理器
 *
 * @author luhan
 */
@Slf4j
@RequiredArgsConstructor
public class IntegrationUserDetailsAuthenticationHandler {

    /**
     * 认证处理
     *
     * @param domain             用户域 ，如b端用户、c端用户等
     * @param authenticationType 认证类型，如密码认证，短信认证等
     * @param token              SpringSecurity的token对象，可以提取用户名、密码等一些用于认证的信息，注意这个token并不是返回给前端的Token
     * @return UnifiedUserDetails是SpringSecurity中的UserDetails对象，用于存放登录成功返回的信息，比如账号基本信息
     * 权限、资源等，此对象会返回给SpringSecurity OAuth2框架，SpringSecurity OAuth2会根据其中的内容自动生成jwt令牌
     */
    public UnifiedUserDetails authentication(String domain, String authenticationType,
                                             UsernamePasswordAuthenticationToken token) {
        String name = token.getName();
        Object password = token.getCredentials();
        if (StringUtils.isBlank(name)) {
            throw new BadCredentialsException("账户名不能为空");
        }
        if (password == null || StringUtils.isBlank(password.toString())) {
            throw new BadCredentialsException("密码不能为空");
        }
        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
        accountLoginDTO.setUsername(name);
        accountLoginDTO.setMobile(name);
        accountLoginDTO.setPassword(password.toString());
        accountLoginDTO.setDomain(domain);

        AccountFeign accountFeign = ApplicationContextHelper.getBean(AccountFeign.class);

        RestResponse<AccountDTO> loginResponse = accountFeign.login(accountLoginDTO);
        if (loginResponse.isSuccessful()) {
            AccountDTO accountDTO = loginResponse.getResult();
            UnifiedUserDetails unifiedUserDetails = new UnifiedUserDetails(accountDTO.getUsername(),
                    password.toString(),
                    AuthorityUtils.createAuthorityList("ROLE_PAGE_A", "PAGE_B"));
            unifiedUserDetails.setMobile(accountDTO.getMobile());
            return unifiedUserDetails;
        }
        throw new BadCredentialsException("登录失败,请重试");
    }

    /**
     * 模拟返回一个UnifiedUserDetails用户
     *
     * @param username
     * @return
     */
    private UnifiedUserDetails getUserDetails(String username) {
        Map<String, UnifiedUserDetails> userDetailsMap = new HashMap<>();
        userDetailsMap.put("admin",
                new UnifiedUserDetails("admin", "111111", AuthorityUtils.createAuthorityList("ROLE_PAGE_A", "PAGE_B")));
        userDetailsMap.put("xufan",
                new UnifiedUserDetails("xufan", "111111", AuthorityUtils.createAuthorityList("ROLE_PAGE_A", "PAGE_B")));

        userDetailsMap.get("admin").setDepartmentId("1");
        userDetailsMap.get("admin").setMobile("18611106983");
        userDetailsMap.get("admin").setTenantId("1");
        Map<String, List<String>> au1 = new HashMap<>();
        au1.put("ROLE1", new ArrayList<>());
        au1.get("ROLE1").add("p1");
        au1.get("ROLE1").add("p2");
        userDetailsMap.get("admin").setUserAuthorities(au1);
        Map<String, Object> payload1 = new HashMap<>();
        payload1.put("res", "res1111111");
        userDetailsMap.get("admin").setPayload(payload1);


        userDetailsMap.get("xufan").setDepartmentId("2");
        userDetailsMap.get("xufan").setMobile("18611106984");
        userDetailsMap.get("xufan").setTenantId("1");
        Map<String, List<String>> au2 = new HashMap<>();
        au2.put("ROLE2", new ArrayList<>());
        au2.get("ROLE2").add("p3");
        au2.get("ROLE2").add("p4");
        userDetailsMap.get("xufan").setUserAuthorities(au2);

        Map<String, Object> payload2 = new HashMap<>();
        payload2.put("res", "res222222");
        userDetailsMap.get("xufan").setPayload(payload2);

        return userDetailsMap.get(username);

    }

}
