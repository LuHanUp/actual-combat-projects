package top.luhancc.saas.hrm.common.model.department;

import lombok.Data;

/**
 * 部门
 *
 * @author luHan
 * @create 2021/5/13 10:38
 * @since 1.0.0
 */
@Data
public class Department {
    private String id;
    /**
     * 父级ID
     */
    private String pid;
    /**
     * 企业ID
     */
    private String companyId;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 部门编码，同级部门不可重复
     */
    private String code;

    /**
     * 负责人ID
     */
    private String managerId;
    /**
     * 负责人名称
     */
    private String manager;

    /**
     * 介绍
     */
    private String introduce;
}
