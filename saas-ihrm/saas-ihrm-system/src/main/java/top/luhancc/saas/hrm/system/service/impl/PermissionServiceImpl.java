package top.luhancc.saas.hrm.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.system.Permission;
import top.luhancc.saas.hrm.common.model.system.PermissionApi;
import top.luhancc.saas.hrm.common.model.system.PermissionMenu;
import top.luhancc.saas.hrm.common.model.system.PermissionPoint;
import top.luhancc.saas.hrm.system.dao.PermissionApiDao;
import top.luhancc.saas.hrm.system.dao.PermissionDao;
import top.luhancc.saas.hrm.system.dao.PermissionMenuDao;
import top.luhancc.saas.hrm.system.dao.PermissionPointDao;
import top.luhancc.saas.hrm.system.domain.param.PermissionParam;
import top.luhancc.saas.hrm.system.domain.query.PermissionQuery;
import top.luhancc.saas.hrm.system.domain.type.PermissionType;
import top.luhancc.saas.hrm.system.mapping.PermissionMapping;
import top.luhancc.saas.hrm.system.service.PermissionService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luHan
 * @create 2021/5/14 10:22
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionDao permissionDao;
    private final IdWorker idWorker;
    private final PermissionApiDao permissionApiDao;
    private final PermissionMenuDao permissionMenuDao;
    private final PermissionPointDao permissionPointDao;
    private final PermissionMapping permissionMapping;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PermissionParam permissionParam) {
        String id = idWorker.nextId() + "";
        // 根据权限类型的不同,保存不同的数据
        Permission permission = permissionMapping.param2Permission(permissionParam);
        permission.setId(id);
        switch (permission.getType()) {
            case PermissionType.MENU:
                PermissionMenu permissionMenu = permissionMapping.param2PermissionMenu(permissionParam);
                permissionMenu.setId(id);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionType.POINT:
                PermissionPoint permissionPoint = permissionMapping.param2PermissionPoint(permissionParam);
                permissionPoint.setId(id);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionType.API:
                PermissionApi permissionApi = permissionMapping.param2PermissionApi(permissionParam);
                permissionApi.setId(id);
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new BaseBusinessException(ResultCode.ARGUMENT_ERROR);
        }
        permissionDao.save(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PermissionParam permissionParam) {
        Permission perm = permissionMapping.param2Permission(permissionParam);
        // 根据权限类型的不同,保存不同的数据
        Permission permission = permissionDao.findById(permissionParam.getId()).get();
        permission.setName(perm.getName());
        permission.setCode(perm.getCode());
        permission.setDescription(perm.getDescription());
        permission.setEnVisible(perm.getEnVisible());
        switch (permission.getType()) {
            case PermissionType.MENU:
                PermissionMenu permissionMenu = permissionMapping.param2PermissionMenu(permissionParam);
                permissionMenu.setId(perm.getId());
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionType.POINT:
                PermissionPoint permissionPoint = permissionMapping.param2PermissionPoint(permissionParam);
                permissionPoint.setId(perm.getId());
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionType.API:
                PermissionApi permissionApi = permissionMapping.param2PermissionApi(permissionParam);
                permissionApi.setId(perm.getId());
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new BaseBusinessException(ResultCode.ARGUMENT_ERROR);
        }
        permissionDao.save(permission);
    }

    @Override
    public PermissionParam findById(String id) {
        Permission permission = permissionDao.findById(id).get();
        PermissionParam permissionParam = permissionMapping.permission2Param(permission);
        switch (permission.getType()) {
            case PermissionType.MENU:
                PermissionMenu permissionMenu = permissionMenuDao.findById(id).get();
                permissionParam.setMenuIcon(permissionMenu.getMenuIcon());
                permissionParam.setMenuOrder(permissionMenu.getMenuOrder());
                break;
            case PermissionType.POINT:
                PermissionPoint permissionPoint = permissionPointDao.findById(id).get();
                permissionParam.setPointClass(permissionPoint.getPointClass());
                permissionParam.setPointIcon(permissionPoint.getPointIcon());
                permissionParam.setPointStatus(permissionPoint.getPointStatus());
                break;
            case PermissionType.API:
                PermissionApi permissionApi = permissionApiDao.findById(id).get();
                permissionParam.setApiLevel(permissionApi.getApiLevel());
                permissionParam.setApiMethod(permissionApi.getApiMethod());
                permissionParam.setApiUrl(permissionApi.getApiUrl());
                break;
            default:
                throw new BaseBusinessException(ResultCode.ARGUMENT_ERROR);
        }
        return permissionParam;
    }

    @Override
    public List<PermissionParam> findAll(String companyId) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        permissionDao.deleteById(id);
        permissionApiDao.deleteById(id);
        permissionPointDao.deleteById(id);
        permissionMenuDao.deleteById(id);
    }

    @Override
    public Page<Permission> findAll(PermissionQuery query) {
        //1.需要查询条件
        Specification<Permission> spec = new Specification<Permission>() {
            /**
             * 动态拼接查询条件
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                //根据父id查询
                if (!StringUtils.isEmpty(query.getPId())) {
                    list.add(criteriaBuilder.equal(root.get("pid").as(String.class), query.getPId()));
                }
                //根据enVisible查询
                if (!StringUtils.isEmpty(query.getEnVisible())) {
                    list.add(criteriaBuilder.equal(root.get("enVisible").as(String.class), query.getEnVisible()));
                }
                //根据类型 type
                if (query.getType() != null) {
                    Integer ty = query.getType();
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                    if (0 == ty) {
                        in.value(1).value(2);
                    } else {
                        in.value(ty);
                    }
                    list.add(in);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
        return permissionDao.findAll(spec, PageRequest.of(query.getPage() - 1, query.getSize()));
    }

    @Override
    public Integer count() {
        return Math.toIntExact(permissionDao.count());
    }
}
