package top.luhancc.saas.ihrm.employee.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.employee.UserCompanyJobs;

/**
 * 岗位信息数据访问接口
 */
public interface UserCompanyJobsDao extends JpaRepository<UserCompanyJobs, String>, JpaSpecificationExecutor<UserCompanyJobs> {
    UserCompanyJobs findByUserId(String userId);

    void deleteByUserId(String userId);
}