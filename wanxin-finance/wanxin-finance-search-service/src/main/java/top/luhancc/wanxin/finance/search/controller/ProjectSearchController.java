package top.luhancc.wanxin.finance.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.api.search.ProjectSearchApi;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.search.ProjectQueryParamsDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.search.service.ProjectIndexService;

/**
 * @author luHan
 * @create 2021/6/21 10:30
 * @since 1.0.0
 */
@RestController
@Api(value = "标的检索服务", tags = "ProjectSearch", description = "标的检索服务API")
public class ProjectSearchController implements ProjectSearchApi {
    @Autowired
    private ProjectIndexService projectIndexService;

    @ApiOperation("检索标的")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectQueryParamsDTO", value = "标的检索参数", required = true, dataType = "ProjectQueryParamsDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sortBy", value = "排序字段", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "顺序", dataType = "String", paramType = "query")})
    @PostMapping(value = "/l/projects/indexes/q")
    @Override
    public RestResponse<PageVO<ProjectDTO>> queryProjectIndex(ProjectQueryParamsDTO projectQueryParamsDTO,
                                                              Integer pageNo, Integer pageSize, String sortBy,
                                                              String order) {
        return RestResponse.success(projectIndexService.queryProjectIndex(projectQueryParamsDTO, pageNo, pageSize,
                sortBy, order));
    }
}
