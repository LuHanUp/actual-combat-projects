package top.luhancc.saas.hrm.company.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.saas.hrm.company.dao.CompanyDao;
import top.luhancc.saas.hrm.company.service.CompanyService;

import java.util.Collections;
import java.util.List;

/**
 * @author luHan
 * @create 2021/4/23 19:35
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends BaseService<Company> implements CompanyService {
    private final CompanyDao companyDao;
    private final IdWorker idWorker;

    @Override
    public void save(Company company) {
        long nextId = idWorker.nextId();
        company.setId(nextId + "");
        company.setAuditState("0"); // 0:未审核 1:已审核
        company.setState(1); // 1:已激活 0:激活
        companyDao.save(company);
    }

    @Override
    public void update(Company company) {
        Company temp = companyDao.findById(company.getId()).get();
        temp.setName(company.getName());
        temp.setCompanyPhone(company.getCompanyPhone());
        companyDao.save(temp);
    }

    @Override
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }

    @Override
    public Company findById(String id) {
        return companyDao.findById(id).get();
    }

    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    @Override
    public List<Company> findAll(String companyId) {
        return Collections.singletonList(findById(companyId));
    }
}
