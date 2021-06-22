package top.luhancc.wanxin.finance.transaction.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.luhancc.wanxin.finance.api.transaction.TransactionApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.*;
import top.luhancc.wanxin.finance.transaction.service.ProjectService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/6/15 14:01
 * @since 1.0.0
 */
@RestController
@Api(value = "交易中心服务", tags = "transaction")
public class TransactionController implements TransactionApi {
    @Autowired
    private ProjectService projectService;

    @ApiOperation("借款人发标")
    @ApiImplicitParam(name = "project", value = "标的信息", required = true,
            dataType = "Project", paramType = "body")
    @PostMapping("/my/projects")
    @Override
    public RestResponse<ProjectDTO> issueTag(@RequestBody ProjectDTO projectDTO) {
        projectDTO = projectService.issueTag(projectDTO);
        return RestResponse.success(projectDTO);
    }

    @ApiOperation("检索标的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectQueryDTO", value = "标的信息查询对象", required = true, dataType = "ProjectQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "order", value = "顺序", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sortBy", value = "排序字段", required = true, dataType = "string", paramType = "query")})
    @PostMapping("/projects/q")
    @Override
    public RestResponse<PageVO<ProjectDTO>> queryProjects(@RequestBody ProjectQueryDTO projectQueryDTO,
                                                          String order, Integer pageNo,
                                                          Integer pageSize, String sortBy) {
        return RestResponse.success(projectService.queryProjects(projectQueryDTO, order, pageNo, pageSize, sortBy));
    }

    @ApiOperation("管理员审核标的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "标的id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "approveStatus", value = "审批状态",
                    required = true, dataType = "ref", paramType = "path")
    })
    @PutMapping("/m/projects/{id}/projectStatus/{approveStatus}")
    @Override
    public RestResponse<String> projectsApprovalStatus(@PathVariable("id") Long id,
                                                       @PathVariable("approveStatus") String approveStatus) {
        return RestResponse.success(projectService.projectsApprovalStatus(id, approveStatus));
    }

    @ApiOperation("通过ids获取多个标的")
    @GetMapping("/projects/{ids}")
    @Override
    public RestResponse<List<ProjectDTO>> queryProjectsIds(@PathVariable String ids) {
        return RestResponse.success(projectService.queryProjectsIds(ids));
    }

    @ApiOperation("根据标的id查询投标记录")
    @GetMapping("/tenders/projects/{id}")
    @Override
    public RestResponse<List<TenderOverviewDTO>> queryTendersByProjectId(@PathVariable Long id) {
        return RestResponse.success(projectService.queryTendersByProjectId(id));
    }

    @ApiOperation("用户投标")
    @ApiImplicitParam(name = "projectInvestDTO", value = "投标信息",
            required = true, dataType = "ProjectInvestDTO", paramType = "body")
    @PostMapping("/my/tenders")
    @Override
    public RestResponse<TenderDTO> createTender(@RequestBody ProjectInvestDTO projectInvestDTO) {
        return RestResponse.success(projectService.createTender(projectInvestDTO));
    }

    @ApiOperation("审核标的满标放款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "标的id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "approveStatus", value = "标的审核状态", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "commission", value = "平台佣金", required = true, dataType = "string", paramType = "query")})
    @PutMapping("/m/loans/{id}/projectStatus/{approveStatus}")
    @Override
    public RestResponse<String> loansApprovalStatus(@PathVariable("id") Long id,
                                                    @PathVariable("approveStatus") String approveStatus,
                                                    String commission) {
        return RestResponse.success(projectService.loansApprovalStatus(id, approveStatus, commission));
    }
}
