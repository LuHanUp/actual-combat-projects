package top.luhancc.wanxin.finance.account.service;

import top.luhancc.wanxin.finance.common.domain.RestResponse;

/**
 * @author luHan
 * @create 2021/6/3 10:25
 * @since 1.0.0
 */
public interface AccountService {

    /**
     * 发送手机验证码并获取校验标识
     *
     * @param mobile 手机号
     * @return
     */
    RestResponse getSMSCode(String mobile);
}
