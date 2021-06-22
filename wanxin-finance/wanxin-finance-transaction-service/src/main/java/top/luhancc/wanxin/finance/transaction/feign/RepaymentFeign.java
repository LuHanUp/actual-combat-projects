package top.luhancc.wanxin.finance.transaction.feign;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;

/**
 * @author luHan
 * @create 2021/6/22 16:20
 * @since 1.0.0
 */
@FeignClient(value = "wanxin-finance-repayment-service", path = "/repayment")
public interface RepaymentFeign {

    @ApiOperation("启动还款")
    @ApiImplicitParam(name = "projectWithTendersDTO", value = "通过id获取标的信息",
            required = true, dataType = "ProjectWithTendersDTO",
            paramType = "body")
    @PostMapping("/l/start-repayment")
    public RestResponse<String> startRepayment(@RequestBody ProjectWithTendersDTO projectWithTendersDTO);
}
