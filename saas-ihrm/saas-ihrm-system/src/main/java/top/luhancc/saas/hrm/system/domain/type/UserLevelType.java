package top.luhancc.saas.hrm.system.domain.type;

/**
 * 用户级别level类别
 *
 * @author luHan
 * @create 2021/5/17 13:38
 * @since 1.0.0
 */
public final class UserLevelType {

    /**
     * 普通用户（需要分配角色）
     */
    public static final String USER = "user";

    /**
     * saas管理员具备所有权限
     */
    public static final String SAAS_ADMIN = "saasAdmin";

    /**
     * 企业管理（创建租户企业的时候添加）
     */
    public static final String CO_ADMIN = "coAdmin";
}
