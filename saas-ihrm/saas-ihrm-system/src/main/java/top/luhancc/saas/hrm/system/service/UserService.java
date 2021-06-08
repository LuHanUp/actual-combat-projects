package top.luhancc.saas.hrm.system.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.system.domain.param.AssignRoleParam;
import top.luhancc.saas.hrm.system.domain.param.LoginParam;
import top.luhancc.saas.hrm.system.domain.param.SocialLoginParam;
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

    /**
     * 通过mobile获取User
     *
     * @param mobile 电话
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 上传用户头像，返回Data URL
     * <p>
     * 同时将用户头像注册进人脸识别库中
     *
     * @param userId 用户id
     * @param file   头像文件
     * @return
     */
    String uploadStaffPhoto(String userId, MultipartFile file);

    /**
     * 手机号、密码登录
     *
     * @param loginParam 登录参数
     * @param authType
     * @return
     */
    String login(LoginParam loginParam, String authType);

    /**
     * 社交方式登录
     *
     * @param socialLoginParam 登录参数
     * @param authType         认证类型
     * @return
     */
    String socialLogin(SocialLoginParam socialLoginParam, String authType);
}
