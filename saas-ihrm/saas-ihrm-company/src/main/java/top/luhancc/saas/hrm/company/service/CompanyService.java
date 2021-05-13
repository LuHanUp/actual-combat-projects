package top.luhancc.saas.hrm.company.service;

import top.luhancc.hrm.common.service.CRUDService;
import top.luhancc.saas.hrm.common.model.company.Company;

import java.util.List;

/**
 * @author luHan
 * @create 2021/4/23 19:35
 * @since 1.0.0
 */
public interface CompanyService extends CRUDService<Company> {
    public List<Company> findAll();
}
