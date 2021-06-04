package top.luhancc.wanxin.finance.consumer.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.api.consumer.ConsumerApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;
import top.luhancc.wanxin.finance.consumer.service.ConsumerService;

/**
 * @author luHan
 * @create 2021/6/4 13:45
 * @since 1.0.0
 */
@RestController
public class ConsumerController implements ConsumerApi {
    @Autowired
    private ConsumerService consumerService;

    @ApiOperation(value = "注册用户信息", httpMethod = "post")
    @ApiImplicitParam(name = "consumerRegisterDTO", value = "用户注册数据",
            paramType = "body", dataTypeClass = ConsumerRegisterDTO.class)
    @PostMapping("/register")
    @Override
    public RestResponse<ConsumerDTO> register(@RequestBody ConsumerRegisterDTO consumerRegisterDTO) {
        ConsumerDTO consumerDTO = consumerService.register(consumerRegisterDTO);
        return RestResponse.success(consumerDTO);
    }
}
