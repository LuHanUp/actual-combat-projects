package top.luhancc.wanxin.finance.transaction.feign;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
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
}
