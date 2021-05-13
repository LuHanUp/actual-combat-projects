package top.luhancc.saas.hrm.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.saas.hrm.common.model.company.Department;
import top.luhancc.saas.hrm.common.model.company.response.department.DeptListResult;
import top.luhancc.saas.hrm.company.service.CompanyService;
import top.luhancc.saas.hrm.company.service.DepartmentService;

import java.util.List;

/**
 * 部门相关的控制器
 *
 * @author luHan
 * @create 2021/5/13 10:21
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/company/department")
@RequiredArgsConstructor
public class DepartmentController extends BaseController {
    private final DepartmentService departmentService;
    private final CompanyService companyService;

    @RequestMapping("/findAll")
    public Result<DeptListResult> findAll() {
        Company company = companyService.findById(companyId);
        List<Department> depts = departmentService.findAllByCompanyId(companyId);
        DeptListResult deptListResult = new DeptListResult(company, depts);
        return Result.success(deptListResult);
    }
}
