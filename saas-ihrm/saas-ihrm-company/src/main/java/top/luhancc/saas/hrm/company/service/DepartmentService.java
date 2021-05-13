package top.luhancc.saas.hrm.company.service;

import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.company.Department;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/13 10:38
 * @since 1.0.0
 */
public interface DepartmentService extends CRUDService<Department> {

    /**
     * 获取指定公司下的所有部门信息
     *
     * @param companyId 公司id
     * @return
     */
    List<Department> findAllByCompanyId(String companyId);
}
