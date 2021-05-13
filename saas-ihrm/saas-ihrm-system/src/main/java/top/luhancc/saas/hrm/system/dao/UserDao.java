package top.luhancc.saas.hrm.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.luhancc.saas.hrm.common.model.system.User;

/**
 * @author luHan
 * @create 2021/5/13 15:34
 * @since 1.0.0
 */
public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    public User findByMobile(String mobile);
}
