package top.luhancc.wanxin.finance.api.transaction;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.*;

import java.util.List;

/**
 * @author luHan
 * @create 2021/6/15 13:54
 * @since 1.0.0
 */
public interface TransactionApi {

    /**
     * 借款人发标
     *
     * @param projectDTO
     * @return
     */
    RestResponse<ProjectDTO> issueTag(ProjectDTO projectDTO);

    /**
     * 检索标的信息
     *
     * @param projectQueryDTO 封装查询条件
     * @param order
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    RestResponse<PageVO<ProjectDTO>> queryProjects(ProjectQueryDTO projectQueryDTO,
                                                   String order, Integer pageNo,
                                                   Integer pageSize, String sortBy);

    /**
     * 管理员审核标的信息
     *
     * @param id
     * @param approveStatus
     * @return
     */
    RestResponse<String> projectsApprovalStatus(Long id, String approveStatus);

    /**
     * 通过ids获取多个标的
     *
     * @param ids
     * @return
     */
    RestResponse<List<ProjectDTO>> queryProjectsIds(String ids);

    /**
     * 根据标的id查询投标记录
     *
     * @param id
     * @return
     */
    RestResponse<List<TenderOverviewDTO>> queryTendersByProjectId(Long id);

    /**
     * 用户投标
     *
     * @param projectInvestDTO
     * @return
     */
    RestResponse<TenderDTO> createTender(ProjectInvestDTO projectInvestDTO);

    /**
     * 审核标的满标放款
     *
     * @param id            标的id
     * @param approveStatus 审核状态
     * @param commission    平台佣金
     * @return
     */
    RestResponse<String> loansApprovalStatus(Long id, String approveStatus, String commission);
}
