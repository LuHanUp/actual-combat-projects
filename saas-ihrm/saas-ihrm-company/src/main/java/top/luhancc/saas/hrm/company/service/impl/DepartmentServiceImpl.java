package top.luhancc.saas.hrm.company.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import top.luhancc.saas.hrm.common.model.department.Department;
import top.luhancc.saas.hrm.company.dao.DepartmentDao;
import top.luhancc.saas.hrm.company.dao.entity.DepartmentDo;
import top.luhancc.saas.hrm.company.mapping.DepartmentMapping;
import top.luhancc.saas.hrm.company.service.DepartmentService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author luHan
 * @create 2021/5/13 10:38
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentDao departmentDao;
    private final DepartmentMapping departmentMapping;

    @Override
    public List<Department> findAllByCompanyId(String companyId) {
        List<DepartmentDo> departmentDos = departmentDao.findAll(new Specification<DepartmentDo>() {

            /**
             * 用户构造查询条件
             *
             * @param root 包含了所有的对象数据
             * @param query 高级查询对象
             * @param criteriaBuilder 构造查询条件
             * @return
             */
            @Override
            public Predicate toPredicate(Root<DepartmentDo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // 根据企业id进行查询
                return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
            }
        });
        return departmentMapping.toListBo(departmentDos);
    }
}
