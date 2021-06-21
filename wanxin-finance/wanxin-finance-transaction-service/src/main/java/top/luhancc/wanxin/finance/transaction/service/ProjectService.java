package top.luhancc.wanxin.finance.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.*;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;

import java.util.List;

/**
 * @author luHan
 * @create 2021/6/15 14:05
 * @since 1.0.0
 */
public interface ProjectService extends IService<Project> {

    /**
     * 借款人发标
     *
     * @param projectDTO
     * @return
     */
    ProjectDTO issueTag(ProjectDTO projectDTO);

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
    PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order, Integer pageNo, Integer pageSize, String sortBy);

    /**
     * 管理员审核标的信息
     *
     * @param id
     * @param approveStatus
     * @return String
     */
    String projectsApprovalStatus(Long id, String approveStatus);

    /**
     * 通过ids获取多个标的
     *
     * @param ids
     * @return
     */
    List<ProjectDTO> queryProjectsIds(String ids);

    /**
     * 根据标的id查询投标记录
     *
     * @param id
     * @return
     */
    List<TenderOverviewDTO> queryTendersByProjectId(Long id);

    /**
     * 用户投标
     *
     * @param projectInvestDTO
     * @return
     */
    TenderDTO createTender(ProjectInvestDTO projectInvestDTO);
}
