package top.luhancc.saas.hrm.system.domain.query;

import lombok.Data;
import top.luhancc.saas.hrm.common.model.PageQuery;

/**
 * @author luHan
 * @create 2021/5/14 10:59
 * @since 1.0.0
 */
@Data
public class PermissionQuery extends PageQuery {

    /**
     * 权限类型：0：菜单+按钮 1：菜单 2：功能点 3：API接口
     */
    private Integer type;

    /**
     * 0:查询所有saas平台的最高权限 1：查询企业的权限
     */
    private Integer enVisible;

    private String pId;
}
