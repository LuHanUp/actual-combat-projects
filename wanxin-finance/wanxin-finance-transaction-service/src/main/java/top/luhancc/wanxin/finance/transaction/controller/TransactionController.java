package top.luhancc.wanxin.finance.transaction.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.api.transaction.TransactionApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;

/**
 * @author luHan
 * @create 2021/6/15 14:01
 * @since 1.0.0
 */
@RestController
@Api(value = "交易中心服务", tags = "transaction")
public class TransactionController implements TransactionApi {

    @ApiOperation("借款人发标")
    @ApiImplicitParam(name = "project", value = "标的信息", required = true,
            dataType = "Project", paramType = "body")
    @PostMapping("/my/projects")
    @Override
    public RestResponse<ProjectDTO> issueTag(ProjectDTO projectDTO) {
        return null;
    }
}
