package top.luhancc.wanxin.finance.repayment.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.api.repayment.RepaymentApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
import top.luhancc.wanxin.finance.repayment.service.RepaymentService;

/**
 * @author luHan
 * @create 2021/6/22 15:38
 * @since 1.0.0
 */
@Api(value = "还款服务", tags = "repayment")
@RestController
public class RepaymentController implements RepaymentApi {

    @Autowired
    private RepaymentService repaymentService;

    @ApiOperation("启动还款")
    @ApiImplicitParam(name = "projectWithTendersDTO", value = "通过id获取标的信息",
            required = true, dataType = "ProjectWithTendersDTO",
            paramType = "body")
    @PostMapping("/l/start-repayment")
    @Override
    public RestResponse<String> startRepayment(@RequestBody ProjectWithTendersDTO projectWithTendersDTO) {
        return RestResponse.success(repaymentService.startRepayment(projectWithTendersDTO));
    }
}
