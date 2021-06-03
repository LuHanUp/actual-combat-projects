package top.luhancc.wanxin.finance.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.api.account.AccountApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;

/**
 * 账户服务controller
 *
 * @author luHan
 * @create 2021/6/3 10:20
 * @since 1.0.0
 */
@RestController
@Api(value = "统一账户服务", tags = "Account")
public class AccountController implements AccountApi {

    @ApiOperation(value = "获取手机号验证码")
    @ApiImplicitParam(name = "mobile", value = "手机号", dataTypeClass = String.class)
    @GetMapping("/sms/{mobile}")
    @Override
    public RestResponse getSMSCode(@PathVariable("mobile") String mobile) {
        return null;
    }
}
