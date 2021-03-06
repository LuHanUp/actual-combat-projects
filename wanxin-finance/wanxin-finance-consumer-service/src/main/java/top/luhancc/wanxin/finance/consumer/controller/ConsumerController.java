package top.luhancc.wanxin.finance.consumer.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.luhancc.wanxin.finance.api.consumer.ConsumerApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.BalanceDetailsDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.BorrowerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.consumer.mapper.entity.Consumer;
import top.luhancc.wanxin.finance.consumer.service.ConsumerService;
import top.luhancc.wanxin.finance.consumer.util.SecurityUtil;

/**
 * @author luHan
 * @create 2021/6/4 13:45
 * @since 1.0.0
 */
@RestController
public class ConsumerController implements ConsumerApi {
    @Autowired
    private ConsumerService consumerService;

    @ApiOperation(value = "注册用户信息", httpMethod = "POST")
    @ApiImplicitParam(name = "consumerRegisterDTO", value = "用户注册数据",
            paramType = "body", dataTypeClass = ConsumerRegisterDTO.class)
    @PostMapping("/register")
    @Override
    public RestResponse<ConsumerDTO> register(@RequestBody ConsumerRegisterDTO consumerRegisterDTO) {
        ConsumerDTO consumerDTO = consumerService.register(consumerRegisterDTO);
        return RestResponse.success(consumerDTO);
    }

    @ApiOperation(value = "生成开户请求数据", httpMethod = "POST")
    @ApiImplicitParam(name = "consumerRequest", value = "开户信息",
            paramType = "body", dataTypeClass = ConsumerRequest.class)
    @PostMapping("/createOpenAccountParam")
    @Override
    public RestResponse<GatewayRequest> createOpenAccountParam(@RequestBody ConsumerRequest consumerRequest) {
        consumerRequest.setMobile(SecurityUtil.getUser().getMobile());
        GatewayRequest gatewayRequest = consumerService.createOpenAccountParam(consumerRequest);
        return RestResponse.success(gatewayRequest);
    }

    @ApiOperation(value = "获取当前登录用户信息", httpMethod = "POST")
    @PostMapping("/l/getCurrConsumer")
    @Override
    public RestResponse<ConsumerDTO> getCurrConsumer() {
        Consumer consumer = consumerService.getByMobile(SecurityUtil.getUser().getMobile());
        ConsumerDTO consumerDTO = new ConsumerDTO();
        BeanUtils.copyProperties(consumer, consumerDTO);
        return RestResponse.success(consumerDTO);
    }

    @ApiOperation("获取借款人用户信息")
    @ApiImplicitParam(name = "id", value = "用户标识", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/my/borrowers/{id}")
    @Override
    public RestResponse<BorrowerDTO> getBorrower(@PathVariable Long id) {
        return RestResponse.success(consumerService.getBorrower(id));
    }

    @ApiOperation("获取用户可用余额")
    @ApiImplicitParam(name = "userNo", value = "用户编码", required = true,
            dataType = "String")
    @GetMapping("/l/balances/{userNo}")
    @Override
    public RestResponse<BalanceDetailsDTO> getBalance(@PathVariable String userNo) {
        return RestResponse.success(consumerService.getBalance(userNo));
    }
}
