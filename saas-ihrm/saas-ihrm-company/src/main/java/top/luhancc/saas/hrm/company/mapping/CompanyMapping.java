package top.luhancc.saas.hrm.company.mapping;


import org.mapstruct.Mapper;
import top.luhancc.hrm.common.mapping.BaseMapping;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.saas.hrm.company.dao.entity.CompanyDo;

/**
 * @author luHan
 * @create 2021/4/23 19:41
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface CompanyMapping extends BaseMapping<Company, CompanyDo> {
}
