package top.luhancc.saas.ihrm.employee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.employee.EmployeePositive;

/**
 * 转正申请数据访问接口
 */
public interface PositiveDao extends JpaRepository<EmployeePositive, String>, JpaSpecificationExecutor<EmployeePositive> {
    EmployeePositive findByUserId(String uid);
}