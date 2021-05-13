package top.luhancc.hrm.common.service;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 公用的service
 *
 * @param <T> T为数据实体类
 * @author luHan
 * @create 2021/5/13 11:17
 * @since 1.0.0
 */
public class BaseService<T> {

    /**
     * 返回通过companyId查询的Specification
     *
     * @param companyId 公司id
     * @return
     */
    protected Specification<T> specByCompanyId(String companyId) {
        return new Specification<T>() {

            /**
             * 用户构造查询条件
             *
             * @param root            包含了所有的对象数据
             * @param query           高级查询对象
             * @param criteriaBuilder 构造查询条件
             * @return
             */
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // 根据企业id进行查询
                return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
            }
        };
    }
}
