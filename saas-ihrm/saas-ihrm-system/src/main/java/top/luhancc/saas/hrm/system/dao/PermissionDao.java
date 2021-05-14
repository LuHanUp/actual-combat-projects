package top.luhancc.saas.hrm.system.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.system.Permission;

import java.util.List;

/**
 * 权限数据访问接口
 *
 * @author luhan
 */
public interface PermissionDao extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByTypeAndPid(int type, String pid);
}