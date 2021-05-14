package top.luhancc.saas.hrm.system.domain.param;

import lombok.Data;

import java.util.List;

/**
 * 分配角色的参数类
 *
 * @author luHan
 * @create 2021/5/14 11:35
 * @since 1.0.0
 */
@Data
public class AssignRoleParam {
    private String userId;
    private List<String> roleIds;
}
