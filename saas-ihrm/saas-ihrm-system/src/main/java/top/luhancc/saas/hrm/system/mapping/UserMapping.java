package top.luhancc.saas.hrm.system.mapping;

import org.mapstruct.Mapper;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.bo.UserToken;

/**
 * @author luHan
 * @create 2021/5/14 15:54
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserMapping {
    UserToken user2UserToken(User user);
}
