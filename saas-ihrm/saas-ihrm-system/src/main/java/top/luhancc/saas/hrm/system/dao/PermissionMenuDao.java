package top.luhancc.saas.hrm.system.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.system.PermissionMenu;

/**
 * 菜单权限数据访问接口
 *
 * @author luhan
 */
public interface PermissionMenuDao extends JpaRepository<PermissionMenu, String>, JpaSpecificationExecutor<PermissionMenu> {

}