package top.luhancc.saas.hrm.company.service;

import top.luhancc.saas.hrm.common.model.company.Company;

import java.util.List;

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

    /**
     * 根据id删除公司信息
     *
     * @param id
     */
    void deleteById(String id);

    Company findById(String id);

    List<Company> findAll();
}
