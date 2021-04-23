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

    }
}
