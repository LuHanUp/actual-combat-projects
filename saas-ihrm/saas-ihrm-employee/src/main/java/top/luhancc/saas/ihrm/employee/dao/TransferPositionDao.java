package top.luhancc.saas.ihrm.employee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.employee.EmployeeTransferPosition;

/**
 * 调岗申请数据访问接口
 */
public interface TransferPositionDao extends JpaRepository<EmployeeTransferPosition, String>, JpaSpecificationExecutor<EmployeeTransferPosition> {
    EmployeeTransferPosition findByUserId(String uid);
}