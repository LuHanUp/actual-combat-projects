package top.luhancc.saas.hrm.company.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.luhancc.saas.hrm.common.model.BaseDo;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author luHan
 * @create 2021/4/23 19:18
 * @since 1.0.0
 */
@Entity
@Table(name = "co_company")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDo extends BaseDo implements Serializable {
    private static final long serialVersionUID = 594829320797158219L;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 企业登录账号ID
     */
    private String managerId;

    /**
     * 当前版本
     */
    private String version;

    /**
     * 续期时间
     */
    private LocalDateTime renewalDate;

    /**
     * 到期时间
     */
    private LocalDateTime expirationDate;

    /**
     * 公司地区
     */
    private String companyArea;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 营业执照-图片ID
     */
    private String businessLicenseId;

    /**
     * 法人代表
     */
    private String legalRepresentative;

    /**
     * 公司电话
     */
    private String companyPhone;

    /**
     * 邮箱
     */
    private String mailbox;

    /**
     * 公司规模
     */
    private String companySize;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 审核状态
     */
    private String auditState;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 当前余额
     */
    private Double balance;
}
