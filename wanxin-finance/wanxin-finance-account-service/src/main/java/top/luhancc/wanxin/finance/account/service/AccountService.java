package top.luhancc.wanxin.finance.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.account.mapper.entity.Account;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountLoginDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountRegisterDTO;

/**
 * @author luHan
 * @create 2021/6/3 10:25
 * @since 1.0.0
 */
public interface AccountService extends IService<Account> {

    /**
     * 发送手机验证码并获取校验标识
     *
     * @param mobile 手机号
     * @return
     */
    RestResponse getSMSCode(String mobile);

    /**
     * 校验手机号和验证码
     *
     * @param mobile 手机号
     * @param key    验证码校验标识
     * @param code   验证码
     * @return
     */
    RestResponse<Integer> checkMobile(String mobile, String key, String code);

    /**
     * 账户注册
     *
     * @param accountRegisterDTO
     * @return
     */
    AccountDTO register(AccountRegisterDTO accountRegisterDTO);

    /**
     * 用户登录
     *
     * @param accountLoginDTO
     * @return
     */
    AccountDTO login(AccountLoginDTO accountLoginDTO);
}
