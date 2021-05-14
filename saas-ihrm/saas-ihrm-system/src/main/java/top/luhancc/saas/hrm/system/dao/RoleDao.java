package top.luhancc.saas.hrm.system.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.system.Role;

/**
 * 角色数据访问接口
 *
 * @author luhan
 */
public interface RoleDao extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

}