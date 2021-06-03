package top.luhancc.wanxin.finance.account.service;

import top.luhancc.wanxin.finance.common.domain.RestResponse;

/**
 * 验证码相关的服务service
 *
 * @author luHan
 * @create 2021/6/3 10:28
 * @since 1.0.0
 */
public interface SmsService {
    /**
     * 发送手机验证码并获取校验标识
     *
     * @param mobile 手机号
     * @return
     */
    RestResponse getSmsCode(String mobile);
}
