package top.luhancc.wanxin.finance.api.account;

import top.luhancc.wanxin.finance.common.domain.RestResponse;

/**
 * 统一账户api接口
 *
 * @author luHan
 * @create 2021/6/3 10:14
 * @since 1.0.0
 */
public interface AccountApi {

    /**
     * 获取手机验证码
     *
     * @param mobile 手机号
     * @return
     */
    RestResponse getSMSCode(String mobile);
}
