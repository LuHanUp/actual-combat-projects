package top.luhancc.saas.hrm.company.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.company.Department;

/**
 * 部门dao接口
 *
 * @author luHan
 * @create 2021/5/13 10:43
 * @since 1.0.0
 */
public interface DepartmentDao extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {

    /**
     * 通过code和公司id查询部门信息
     *
     * @param code
     * @param companyId
     * @return
     */
    Department findByCodeAndCompanyId(String code, String companyId);
}
