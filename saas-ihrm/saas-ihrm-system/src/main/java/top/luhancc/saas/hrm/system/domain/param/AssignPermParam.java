package top.luhancc.saas.hrm.system.domain.param;

import lombok.Data;

import java.util.List;

/**
 * 分配权限参数类
 *
 * @author luHan
 * @create 2021/5/14 14:50
 * @since 1.0.0
 */
@Data
public class AssignPermParam {
    private String roleId;
    private List<String> permsIds;
}
