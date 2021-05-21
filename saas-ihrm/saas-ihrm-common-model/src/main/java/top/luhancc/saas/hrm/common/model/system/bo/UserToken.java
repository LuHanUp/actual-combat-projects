package top.luhancc.saas.hrm.common.model.system.bo;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户token类，只保存用户重要的信息,减少jwt生成token的长度
 *
 * @author luHan
 * @create 2021/5/21 14:38
 * @since 1.0.0
 */
@Data
public class UserToken implements Serializable {

    /**
     * 数据库主键
     */
    @Id
    private String id;

    //其他属性
    private static final long serialVersionUID = -1l;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户名称
     */
    private String username;

    private String companyId;

    private String companyName;

    /**
     * level
     * String
     * saasAdmin：saas管理员具备所有权限
     * coAdmin：企业管理（创建租户企业的时候添加）
     * user：普通用户（需要分配角色）
     */
    private String level;
}
