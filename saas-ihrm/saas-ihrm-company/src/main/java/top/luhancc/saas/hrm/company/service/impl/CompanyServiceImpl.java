package top.luhancc.saas.hrm.company.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.saas.hrm.company.dao.CompanyDao;
import top.luhancc.saas.hrm.company.dao.entity.CompanyDo;
import top.luhancc.saas.hrm.company.mapping.CompanyMapping;
import top.luhancc.saas.hrm.company.service.CompanyService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author luHan
 * @create 2021/4/23 19:35
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyDao companyDao;
    private final IdWorker idWorker;
    private final CompanyMapping companyMapping;

    @Override
    public void add(Company company) {
        long nextId = idWorker.nextId();
        company.setId(nextId + "");
        company.setAuditState("0"); // 0:未审核 1:已审核
        company.setState(1); // 1:已激活 0:激活

        CompanyDo companyDo = companyMapping.toDo(company);
        companyDo.setCreateTime(LocalDateTime.now());
        companyDao.save(companyDo);
    }

    @Override
    public void update(Company company) {
        CompanyDo oldCompanyDo = companyDao.findById(company.getId()).get();
        oldCompanyDo.setUpdateTime(LocalDateTime.now());
        oldCompanyDo.setName(company.getName());
        oldCompanyDo.setCompanyPhone(company.getCompanyPhone());
        companyDao.save(oldCompanyDo);
    }

    @Override
    public void deleteById(String id) {
        companyDao.deleteById(id);
    }

    @Override
    public Company findById(String id) {
        return companyMapping.toBo(companyDao.findById(id).get());
    }

    @Override
    public List<Company> findAll() {
        return companyMapping.toListBo(companyDao.findAll());
    }
}
