package top.luhancc.saas.ihrm.employee.service;

import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.employee.UserCompanyPersonal;
import top.luhancc.saas.hrm.common.model.employee.response.EmployeeReportResult;

import java.util.List;

/**
 * 员工详情service
 *
 * @author luHan
 * @create 2021/5/21 10:31
 * @since 1.0.0
 */
public interface UserCompanyPersonalService extends CRUDService<UserCompanyPersonal> {
    List<EmployeeReportResult> findByReport(String companyId, String month);
}
