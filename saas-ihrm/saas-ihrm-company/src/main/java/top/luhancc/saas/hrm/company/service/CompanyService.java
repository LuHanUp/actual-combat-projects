package top.luhancc.saas.hrm.company.service;

import top.luhancc.saas.hrm.common.model.company.Company;

/**
 * @author luHan
 * @create 2021/4/23 19:35
 * @since 1.0.0
 */
public interface CompanyService {

    /**
     * 保存公司信息
     *
     * @param company
     */
    void add(Company company);

    /**
     * 修改公司信息
     *
     * @param company
     */
    void update(Company company);
}
