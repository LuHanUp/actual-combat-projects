package top.luhancc.saas.hrm.company.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.luhancc.saas.hrm.common.model.company.Department;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.saas.hrm.company.dao.DepartmentDao;
import top.luhancc.saas.hrm.company.dao.entity.DepartmentDo;
import top.luhancc.saas.hrm.company.mapping.DepartmentMapping;
import top.luhancc.saas.hrm.company.service.DepartmentService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/13 10:38
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends BaseService<DepartmentDo> implements DepartmentService {
    private final DepartmentDao departmentDao;
    private final DepartmentMapping departmentMapping;

    @Override
    public List<Department> findAllByCompanyId(String companyId) {
        List<DepartmentDo> departmentDos = departmentDao.findAll(specByCompanyId(companyId));
        return departmentMapping.toListBo(departmentDos);
    }
}
