package top.luhancc.saas.hrm.system.domain.query;

import lombok.Data;
import top.luhancc.saas.hrm.common.model.PageQuery;

/**
 * 用户信息查询条件类
 *
 * @author luHan
 * @create 2021/5/13 15:37
 * @since 1.0.0
 */
@Data
public class UserQuery extends PageQuery {

    /**
     * 是否分配部门: 0-未分配 1-已分配
     */
    private Integer hasDept;
    private String departmentId;
}
