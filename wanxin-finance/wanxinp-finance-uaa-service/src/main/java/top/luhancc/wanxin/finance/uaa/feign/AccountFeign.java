package top.luhancc.wanxin.finance.uaa.feign;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountLoginDTO;

/**
 * @author luHan
 * @create 2021/6/9 10:50
 * @since 1.0.0
 */
@FeignClient(contextId = "AccountFeign", value = "wanxin-finance-account-service", path = "/account")
public interface AccountFeign {

    @ApiOperation(value = "用户登录")
    @ApiImplicitParam(name = "accountLoginDTO", value = "用户登录信息", required = true,
            dataTypeClass = AccountLoginDTO.class, paramType = "body")
    @PostMapping("/login")
    public RestResponse<AccountDTO> login(@RequestBody AccountLoginDTO accountLoginDTO);
}
