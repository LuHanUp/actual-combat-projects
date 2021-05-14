package top.luhancc.saas.hrm.common.model.system.response;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import top.luhancc.saas.hrm.common.model.system.Role;
import top.luhancc.saas.hrm.common.model.system.User;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/5/14 15:51
 * @since 1.0.0
 */
@Data
public class UserResult {

    /**
     * 数据库主键
     */
    private String id;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 启用状态 0为禁用 1为启用
     */
    private Integer enableState;

    /**
     * 创建时间
     */
    private Date createTime;

    private String companyId;

    private String companyName;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 入职时间
     */
    private Date timeOfEntry;

    /**
     * 聘用形式
     */
    private Integer formOfEmployment;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 管理形式
     */
    private String formOfManagement;

    /**
     * 工作城市
     */
    private String workingCity;

    /**
     * 转正时间
     */
    private Date correctionTime;

    /**
     * 在职状态 1.在职  2.离职
     */
    private Integer inServiceStatus;

    private String departmentName;

    /**
     * level
     * String
     * saasAdmin：saas管理员具备所有权限
     * coAdmin：企业管理（创建租户企业的时候添加）
     * user：普通用户（需要分配角色）
     */
    private String level;

    private String staffPhoto;//用户头像

    List<String> roleIds;

    public UserResult(User user) {
        BeanUtils.copyProperties(user, this);
        this.roleIds = user.getRoles().stream().map(Role::getId).collect(Collectors.toList());
    }
}
