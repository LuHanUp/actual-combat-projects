package top.luhancc.wanxin.finance.depository.agent.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.api.depository.agent.DepositoryAgentApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.depository.agent.service.DepositoryRecordService;

/**
 * @author luHan
 * @create 2021/6/10 15:37
 * @since 1.0.0
 */
@RestController
public class DepositoryAgentController implements DepositoryAgentApi {
    @Autowired
    private DepositoryRecordService depositoryRecordService;

    @ApiOperation(value = "生成开户请求数据", httpMethod = "post")
    @ApiImplicitParam(name = "consumerRequest", value = "开户信息",
            paramType = "body", dataTypeClass = ConsumerRequest.class)
    @PostMapping("/inner/createOpenAccountParam")
    @Override
    public RestResponse<GatewayRequest> createOpenAccountParam(@RequestBody ConsumerRequest consumerRequest) {
        GatewayRequest gatewayRequest = depositoryRecordService.createOpenAccountParam(consumerRequest);
        return RestResponse.success(gatewayRequest);
    }
}
