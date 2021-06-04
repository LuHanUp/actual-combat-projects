package top.luhancc.wanxin.finance.api.account;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountRegisterDTO;

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
     * @return 校验标识
     */
    RestResponse getSMSCode(String mobile);


    /**
     * 校验手机号和验证码
     *
     * @param mobile 手机号
     * @param key    校验标识
     * @param code   验证码
     * @return
     */
    RestResponse<Integer> checkMobile(String mobile, String key, String code);

    /**
     * 用户注册
     *
     * @param accountRegisterDTO
     * @return
     */
    RestResponse<AccountDTO> register(AccountRegisterDTO accountRegisterDTO);
}
