package top.luhancc.wanxin.finance.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.account.service.AccountService;
import top.luhancc.wanxin.finance.account.service.SmsService;
import top.luhancc.wanxin.finance.common.domain.RestResponse;

/**
 * @author luHan
 * @create 2021/6/3 14:47
 * @since 1.0.0
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private SmsService smsService;

    @Override
    public RestResponse getSMSCode(String mobile) {
        return smsService.getSmsCode(mobile);
    }
}
