package top.luhancc.saas.hrm.system.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.system.PermissionApi;

/**
 * api权限数据访问接口
 *
 * @author luhan
 */
public interface PermissionApiDao extends JpaRepository<PermissionApi, String>, JpaSpecificationExecutor<PermissionApi> {

}