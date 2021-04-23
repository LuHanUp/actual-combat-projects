package top.luhancc.saas.hrm.company.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.company.dao.entity.CompanyDo;

/**
 * co_company表的dao接口
 * <p>
 * 1. 实现{@code JpaRepository<实体类, 主键类型>}
 * 2. JpaSpecificationExecutor:动态生成query
 *
 * @author luHan
 * @create 2021/4/23 19:27
 * @since 1.0.0
 */
public interface CompanyDao extends JpaRepository<CompanyDo, String>, JpaSpecificationExecutor<CompanyDo> {
}
