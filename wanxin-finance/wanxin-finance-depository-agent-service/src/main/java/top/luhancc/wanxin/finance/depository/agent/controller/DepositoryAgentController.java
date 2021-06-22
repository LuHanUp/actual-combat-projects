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
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.*;
import top.luhancc.wanxin.finance.common.domain.model.repayment.LoanRequest;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
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

    @ApiOperation(value = "向存管系统发送标的信息")
    @ApiImplicitParam(name = "projectDTO", value = "向存管系统发送标的信息",
            required = true, dataType = "ProjectDTO", paramType = "body")
    @PostMapping("/l/createProject")
    @Override
    public RestResponse<String> createProject(@RequestBody ProjectDTO projectDTO) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = depositoryRecordService.createProject(projectDTO);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setResult(depositoryResponse.getRespData().getRespCode());
        restResponse.setMsg(depositoryResponse.getRespData().getRespMsg());
        return restResponse;
    }

    @ApiOperation(value = "投标预授权处理")
    @ApiImplicitParam(name = "userAutoPreTransactionRequest", value = "平台向存管系统发送标的信息",
            required = true, dataType = "UserAutoPreTransactionRequest", paramType = "body")
    @PostMapping("/l/user-auto-pre-transaction")
    @Override
    public RestResponse<String> userAutoPreTransaction(@RequestBody UserAutoPreTransactionRequest userAutoPreTransactionRequest) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse =
                depositoryRecordService.userAutoPreTransaction(userAutoPreTransactionRequest);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setResult(depositoryResponse.getRespData().getRespCode());
        restResponse.setMsg(depositoryResponse.getRespData().getRespMsg());
        return restResponse;
    }

    @ApiOperation(value = "审核标的满标放款")
    @ApiImplicitParam(name = "loanRequest", value = "标的满标放款信息", required = true, dataType = "LoanRequest", paramType = "body")
    @PostMapping("l/confirm-loan")
    @Override
    public RestResponse<String> confirmLoan(@RequestBody LoanRequest loanRequest) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse =
                depositoryRecordService.confirmLoan(loanRequest);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setResult(depositoryResponse.getRespData().getRespCode());
        restResponse.setMsg(depositoryResponse.getRespData().getRespMsg());
        return restResponse;
    }

    @ApiOperation(value = "修改标的状态")
    @ApiImplicitParam(name = "modifyProjectStatusDTO", value = "修改标的状态DTO",
            required = true, dataType = "ModifyProjectStatusDTO",
            paramType = "body")
    @PostMapping("l/modify-project-status")
    @Override
    public RestResponse<String> modifyProjectStatus(@RequestBody ModifyProjectStatusDTO modifyProjectStatusDTO) {
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse =
                depositoryRecordService.modifyProjectStatus(modifyProjectStatusDTO);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setResult(depositoryResponse.getRespData().getRespCode());
        restResponse.setMsg(depositoryResponse.getRespData().getRespMsg());
        return restResponse;
    }
}
