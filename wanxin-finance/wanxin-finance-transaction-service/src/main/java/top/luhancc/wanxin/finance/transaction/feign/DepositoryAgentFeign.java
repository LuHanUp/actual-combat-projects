package top.luhancc.wanxin.finance.transaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;

/**
 * @author luHan
 * @create 2021/6/15 17:44
 * @since 1.0.0
 */
@FeignClient(value = "wanxin-finance-depository-agent-service", path = "/depository-agent")
public interface DepositoryAgentFeign {
    RestResponse<String> createProject(ProjectDTO projectDTO);
}
