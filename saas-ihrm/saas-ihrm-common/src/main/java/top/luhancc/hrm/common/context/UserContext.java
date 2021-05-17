package top.luhancc.hrm.common.context;

import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.saas.hrm.common.model.system.User;

/**
 * 用户上下文信息,只有在登录之后才会有
 *
 * @author luHan
 * @create 2021/5/17 11:36
 * @since 1.0.0
 */
public final class UserContext {
    private static final InheritableThreadLocal<User> USER_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static User getCurrentUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void setCurrentUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static String getUserId() {
        User loginUser = getCurrentUser();
        if (loginUser == null) {
            throw new BaseBusinessException(ResultCode.UNAUTHENTICATED);
        }
        return loginUser.getId();
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
