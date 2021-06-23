package top.luhancc.wanxin.finance.repayment.feign;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.ModifyProjectStatusDTO;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.UserAutoPreTransactionRequest;
import top.luhancc.wanxin.finance.common.domain.model.repayment.LoanRequest;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;

/**
 * @author luHan
 * @create 2021/6/15 17:44
 * @since 1.0.0
 */
@FeignClient(value = "wanxin-finance-depository-agent-service", path = "/depository-agent")
public interface DepositoryAgentFeign {

    @ApiOperation(value = "向存管系统发送标的信息")
    @ApiImplicitParam(name = "projectDTO", value = "向存管系统发送标的信息",
            required = true, dataType = "ProjectDTO", paramType = "body")
    @PostMapping("/l/createProject")
    public RestResponse<String> createProject(ProjectDTO projectDTO);

    @ApiOperation(value = "预授权处理")
    @ApiImplicitParam(name = "userAutoPreTransactionRequest", value = "平台向存管系统发送标的信息",
            required = true, dataType = "UserAutoPreTransactionRequest", paramType = "body")
    @PostMapping("/l/user-auto-pre-transaction")
    public RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest request);

    @ApiOperation(value = "审核标的满标放款")
    @ApiImplicitParam(name = "loanRequest", value = "标的满标放款信息", required = true, dataType = "LoanRequest", paramType = "body")
    @PostMapping("l/confirm-loan")
    public RestResponse<String> confirmLoan(@RequestBody LoanRequest loanRequest);

    @ApiOperation(value = "修改标的状态")
    @ApiImplicitParam(name = "modifyProjectStatusDTO", value = "修改标的状态DTO",
            required = true, dataType = "ModifyProjectStatusDTO",
            paramType = "body")
    @PostMapping("l/modify-project-status")
    public RestResponse<String> modifyProjectStatus(@RequestBody ModifyProjectStatusDTO modifyProjectStatusDTO);
}
