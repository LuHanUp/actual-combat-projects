package top.luhancc.saas.hrm.company.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.luhancc.hrm.common.service.BaseService;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.company.Department;
import top.luhancc.saas.hrm.company.dao.DepartmentDao;
import top.luhancc.saas.hrm.company.service.DepartmentService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/13 10:38
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends BaseService<Department> implements DepartmentService {
    private final DepartmentDao departmentDao;
    private final IdWorker idWorker;

    @Override
    public List<Department> findAllByCompanyId(String companyId) {
        return departmentDao.findAll(specByCompanyId(companyId));
    }

    @Override
    public void save(Department department) {
        //设置主键的值
        String id = idWorker.nextId() + "";
        department.setId(id);
        //调用dao保存部门
        departmentDao.save(department);
    }

    @Override
    public void update(Department department) {
        //1.根据id查询部门
        Department dept = departmentDao.findById(department.getId()).get();
        //2.设置部门属性
        dept.setCode(department.getCode());
        dept.setIntroduce(department.getIntroduce());
        dept.setName(department.getName());
        //3.更新部门
        departmentDao.save(dept);
    }

    @Override
    public Department findById(String id) {
        return departmentDao.findById(id).get();
    }

    @Override
    public List<Department> findAll(String companyId, UserQuery userQuery) {
        return departmentDao.findAll(specByCompanyId(companyId));
    }

    @Override
    public void deleteById(String id) {
        departmentDao.deleteById(id);
    }
}
