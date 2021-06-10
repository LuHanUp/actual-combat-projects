package top.luhancc.wanxin.finance.consumer.feign.account;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountRegisterDTO;

/**
 * @author luHan
 * @create 2021/6/4 16:43
 * @since 1.0.0
 */
@FeignClient(contextId = "AccountFeign", value = "wanxin-finance-account-service", path = "/account")
public interface AccountFeign {

    /**
     * 用户注册
     *
     * @param accountRegisterDTO
     * @return
     */
    @PostMapping("/register")
    @Hmily
    RestResponse<AccountDTO> register(@RequestBody AccountRegisterDTO accountRegisterDTO);
}
