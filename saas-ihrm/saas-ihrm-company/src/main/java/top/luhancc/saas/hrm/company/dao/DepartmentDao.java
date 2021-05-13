package top.luhancc.saas.hrm.company.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.company.dao.entity.DepartmentDo;

/**
 * 部门dao接口
 *
 * @author luHan
 * @create 2021/5/13 10:43
 * @since 1.0.0
 */
public interface DepartmentDao extends JpaRepository<DepartmentDo, String>, JpaSpecificationExecutor<DepartmentDo> {

    /**
     * 通过code和公司id查询部门信息
     *
     * @param code
     * @param companyId
     * @return
     */
    DepartmentDo findByCodeAndCompanyId(String code, String companyId);
}
