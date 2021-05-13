package top.luhancc.saas.hrm.company.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.luhancc.saas.hrm.common.model.BaseDo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * (Department)实体类
 *
 * @author luHan
 * @create 2021/5/13 10:45
 * @since 1.0.0
 */
@Entity
@Table(name = "co_department")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDo extends BaseDo implements Serializable {
    private static final long serialVersionUID = -9084332495284489553L;

    @Id
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