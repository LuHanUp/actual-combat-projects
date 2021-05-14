package top.luhancc.saas.hrm.common.model.system.response;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.Role;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/5/14 15:51
 * @since 1.0.0
 */
@Data
public class RoleResult {
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 说明
     */
    private String description;
    /**
     * 企业id
     */
    private String companyId;

    private List<String> permIds;

    public RoleResult(Role role) {
        BeanUtils.copyProperties(role, this);
        this.permIds = role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList());
    }
}
