package top.luhancc.wanxin.finance.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.account.domain.AccountErrorCode;
import top.luhancc.wanxin.finance.account.mapper.AccountMapper;
import top.luhancc.wanxin.finance.account.mapper.entity.Account;
import top.luhancc.wanxin.finance.account.service.AccountService;
import top.luhancc.wanxin.finance.account.service.SmsService;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.CommonErrorCode;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountLoginDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountRegisterDTO;

/**
 * @author luHan
 * @create 2021/6/3 14:47
 * @since 1.0.0
 */
@Service
@Slf4j
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

    @Override
    @Hmily(confirmMethod = "registerConfirm", cancelMethod = "registerCancel")
    public AccountDTO register(AccountRegisterDTO accountRegisterDTO) {
        Account account = new Account();
        BeanUtils.copyProperties(accountRegisterDTO, account);
        account.setDomain("c");
        this.save(account);
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
    }

    public void confirmRegister(AccountRegisterDTO registerDTO) {
        log.info("execute confirmRegister");
    }

    public void cancelRegister(AccountRegisterDTO registerDTO) {
        log.info("execute cancelRegister");
        //删除账号
        remove(Wrappers.<Account>lambdaQuery().eq(Account::getUsername,
                registerDTO.getUsername()));
    }

    @Override
    public AccountDTO login(AccountLoginDTO accountLoginDTO) {
        LambdaQueryWrapper<Account> queryWrapper = Wrappers.lambdaQuery(Account.class);
        if ("C".equalsIgnoreCase(accountLoginDTO.getDomain())) {
            queryWrapper.eq(Account::getMobile, accountLoginDTO.getMobile());
        } else {
            queryWrapper.eq(Account::getUsername, accountLoginDTO.getUsername());
        }
        queryWrapper.eq(Account::getPassword, accountLoginDTO.getPassword());
        Account account = this.getOne(queryWrapper);
        if (account == null) {
            throw new BusinessException(AccountErrorCode.E_130105);
        }
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
    }
}
