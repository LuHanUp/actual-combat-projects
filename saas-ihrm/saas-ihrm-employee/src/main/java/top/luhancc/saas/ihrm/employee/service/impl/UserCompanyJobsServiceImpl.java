package top.luhancc.saas.ihrm.employee.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.luhancc.saas.hrm.common.model.employee.UserCompanyJobs;
import top.luhancc.saas.ihrm.employee.dao.UserCompanyJobsDao;
import top.luhancc.saas.ihrm.employee.service.UserCompanyJobsService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/21 10:38
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserCompanyJobsServiceImpl implements UserCompanyJobsService {
    private final UserCompanyJobsDao userCompanyJobsDao;

    @Override
    public void save(UserCompanyJobs userCompanyJobs) {
        userCompanyJobsDao.save(userCompanyJobs);
    }

    @Override
    public void update(UserCompanyJobs userCompanyJobs) {
        UserCompanyJobs oldUserCompanyJobs = userCompanyJobsDao.findByUserId(userCompanyJobs.getUserId());
        if (oldUserCompanyJobs != null) {
            userCompanyJobsDao.save(userCompanyJobs);
        }
    }

    @Override
    public UserCompanyJobs findById(String userId) {
        return userCompanyJobsDao.findByUserId(userId);
    }

    @Override
    public List<UserCompanyJobs> findAll(String companyId) {
        throw new UnsupportedOperationException("员工岗位信息不允许查询所有");
    }

    @Override
    public void deleteById(String userId) {
        userCompanyJobsDao.deleteByUserId(userId);
    }
}
