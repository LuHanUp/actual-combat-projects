package top.luhancc.saas.hrm.system.domain.param;

import lombok.Data;

import javax.persistence.Id;

/**
 * 权限数据的参数类
 *
 * @author luHan
 * @create 2021/5/14 10:27
 * @since 1.0.0
 */
@Data
public class PermissionParam {
    /**
     * 主键
     */
    @Id
    private String id;
    /**
     * 权限名称
     */
    private String name;
    /**
     * 权限类型 1为菜单 2为功能 3为API
     */
    private Integer type;

    /**
     * 权限编码，根据权限编码code进行比较当前用户是否拥有这个权限
     */
    private String code;

    /**
     * 权限描述
     */
    private String description;

    private String pid;

    // ===================api 权限相关属性======================
    /**
     * 链接
     */
    private String apiUrl;
    /**
     * 请求类型
     */
    private String apiMethod;
    /**
     * 权限等级，1为通用接口权限，2为需校验接口权限
     */
    private String apiLevel;


    // ===================menu 权限相关属性======================
    //展示图标
    private String menuIcon;

    //排序号
    private String menuOrder;


    // ===================point 权限相关属性======================
    /**
     * 权限代码
     */
    private String pointClass;

    private String pointIcon;

    private String pointStatus;
}
