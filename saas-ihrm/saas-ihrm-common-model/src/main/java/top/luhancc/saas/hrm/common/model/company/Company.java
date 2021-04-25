package top.luhancc.saas.hrm.common.model.company;

import lombok.Data;
import top.luhancc.saas.hrm.common.model.validate.group.UpdateGroup;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 公司类
 *
 * @author luHan
 * @create 2021/4/23 18:55
 * @since 1.0.0
 */
@Data
public class Company {

    /**
     * 主键id
     */
    @NotNull(message = "主键id不能为空", groups = {UpdateGroup.class})
    private String id;

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
