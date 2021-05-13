package top.luhancc.saas.hrm.common.model.response.department;

import lombok.Data;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.saas.hrm.common.model.department.Department;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/13 11:01
 * @since 1.0.0
 */
@Data
public class DeptListResult {
    private String companyId;
    private String companyName;
    private String companyManage;
    private List<Department> depts;

    public DeptListResult() {
    }

    public DeptListResult(Company company, List<Department> depts) {
        this.depts = depts;
        this.companyId = company.getId();
        this.companyName = company.getName();
        this.companyManage = company.getManagerId();
    }
}
