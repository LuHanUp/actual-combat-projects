package top.luhancc.wanxin.finance.transaction.feign;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.BalanceDetailsDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;

/**
 * @author luHan
 * @create 2021/6/15 14:52
 * @since 1.0.0
 */
@FeignClient(value = "wanxin-finance-consumer-service", path = "/consumer")
public interface ConsumerFeign {

    @ApiOperation(value = "获取当前登录用户信息", httpMethod = "POST")
    @PostMapping("/l/getCurrConsumer")
    public RestResponse<ConsumerDTO> getCurrConsumer();

    @ApiOperation("获取用户可用余额")
    @ApiImplicitParam(name = "userNo", value = "用户编码", required = true,
            dataType = "String")
    @GetMapping("/l/balances/{userNo}")
    public RestResponse<BalanceDetailsDTO> getBalance(@PathVariable String userNo);
}
