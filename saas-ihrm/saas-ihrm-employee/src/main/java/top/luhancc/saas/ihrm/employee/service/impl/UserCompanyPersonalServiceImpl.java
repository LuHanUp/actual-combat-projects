package top.luhancc.saas.ihrm.employee.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.luhancc.saas.hrm.common.model.employee.UserCompanyPersonal;
import top.luhancc.saas.hrm.common.model.employee.response.EmployeeReportResult;
import top.luhancc.saas.ihrm.employee.dao.UserCompanyPersonalDao;
import top.luhancc.saas.ihrm.employee.service.UserCompanyPersonalService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/21 10:32
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserCompanyPersonalServiceImpl implements UserCompanyPersonalService {
    private final UserCompanyPersonalDao userCompanyPersonalDao;

    @Override
    public void save(UserCompanyPersonal userCompanyPersonal) {
        userCompanyPersonalDao.save(userCompanyPersonal);
    }

    @Override
    public void update(UserCompanyPersonal userCompanyPersonal) {
        String userId = userCompanyPersonal.getUserId();
        UserCompanyPersonal oldUserCompanyPersonal = userCompanyPersonalDao.findByUserId(userId);
        if (oldUserCompanyPersonal != null) {
            userCompanyPersonalDao.save(userCompanyPersonal);
        }
    }

    @Override
    public UserCompanyPersonal findById(String userId) {
        return userCompanyPersonalDao.findByUserId(userId);
    }

    @Override
    public List<UserCompanyPersonal> findAll(String companyId) {
        throw new UnsupportedOperationException("员工详细信息不允许查询所有");
    }

    @Override
    public void deleteById(String userId) {
        userCompanyPersonalDao.deleteByUserId(userId);
    }

    @Override
    public List<EmployeeReportResult> findByReport(String companyId, String month) {
        return userCompanyPersonalDao.findByReport(companyId, month + "%");
    }
}
