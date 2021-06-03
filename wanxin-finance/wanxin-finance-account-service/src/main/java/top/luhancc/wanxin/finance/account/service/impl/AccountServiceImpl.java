package top.luhancc.wanxin.finance.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.account.mapper.AccountMapper;
import top.luhancc.wanxin.finance.account.mapper.entity.Account;
import top.luhancc.wanxin.finance.account.service.AccountService;
import top.luhancc.wanxin.finance.account.service.SmsService;
import top.luhancc.wanxin.finance.common.domain.CommonErrorCode;
import top.luhancc.wanxin.finance.common.domain.RestResponse;

/**
 * @author luHan
 * @create 2021/6/3 14:47
 * @since 1.0.0
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Autowired
    private SmsService smsService;

    @Override
    public RestResponse getSMSCode(String mobile) {
        return smsService.getSmsCode(mobile);
    }

    @Override
    public RestResponse<Integer> checkMobile(String mobile, String key, String code) {
        boolean verify = smsService.verifyCode(key, code);
        if (verify) {
            // 校验手机号
            LambdaQueryWrapper<Account> queryWrapper = Wrappers.lambdaQuery(Account.class).eq(Account::getMobile, mobile);
            int count = this.count(queryWrapper);
            return RestResponse.success(count > 0 ? 1 : 0);
        }
        return RestResponse.validfail(CommonErrorCode.E_100102.getDesc());
    }
}
