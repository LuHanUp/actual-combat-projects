package top.luhancc.wanxin.finance.consumer.feign.depository.agent;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;

/**
 * @author luHan
 * @create 2021/6/10 15:45
 * @since 1.0.0
 */
@FeignClient(value = "wanxin-finance-depository-agent-service", path = "/depository/agent")
public interface DepositoryAgentFeign {

    @ApiOperation(value = "生成开户请求数据", httpMethod = "post")
    @ApiImplicitParam(name = "consumerRequest", value = "开户信息",
            paramType = "body", dataTypeClass = ConsumerRequest.class)
    @PostMapping("/inner/createOpenAccountParam")
    public RestResponse<GatewayRequest> createOpenAccountParam(@RequestBody ConsumerRequest consumerRequest);
}
