package top.luhancc.wanxin.finance.transaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.CodePrefixCode;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.StatusCode;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectQueryDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.TenderOverviewDTO;
import top.luhancc.wanxin.finance.common.util.CodeNoUtil;
import top.luhancc.wanxin.finance.common.util.CommonUtil;
import top.luhancc.wanxin.finance.transaction.common.constant.ProjectCode;
import top.luhancc.wanxin.finance.transaction.common.constant.RepaymentWayCode;
import top.luhancc.wanxin.finance.transaction.common.constant.TransactionErrorCode;
import top.luhancc.wanxin.finance.transaction.feign.ConsumerFeign;
import top.luhancc.wanxin.finance.transaction.feign.DepositoryAgentFeign;
import top.luhancc.wanxin.finance.transaction.mapper.ProjectMapper;
import top.luhancc.wanxin.finance.transaction.mapper.TenderMapper;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Tender;
import top.luhancc.wanxin.finance.transaction.service.ConfigService;
import top.luhancc.wanxin.finance.transaction.service.ProjectService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/6/15 14:05
 * @since 1.0.0
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    private ConsumerFeign consumerFeign;
    @Autowired
    private ConfigService configService;
    @Autowired
    private DepositoryAgentFeign depositoryAgentFeign;
    @Autowired
    private TenderMapper tenderMapper;

    @Override
    public ProjectDTO issueTag(ProjectDTO projectDTO) {
        RestResponse<ConsumerDTO> restResponse = consumerFeign.getCurrConsumer();
        if (restResponse.isSuccessful()) {
            ConsumerDTO consumerDTO = restResponse.getResult();
            projectDTO.setConsumerId(consumerDTO.getId());
            projectDTO.setUserNo(consumerDTO.getUserNo());

            projectDTO.setProjectNo(CodeNoUtil.getNo(CodePrefixCode.CODE_PROJECT_PREFIX));
            projectDTO.setProjectStatus(ProjectCode.COLLECTING.getCode());
            projectDTO.setStatus(StatusCode.STATUS_OUT.getCode());

            projectDTO.setCreateDate(LocalDateTime.now());
            projectDTO.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getCode());
            projectDTO.setType("NEW");


            Project project = new Project();
            BeanUtils.copyProperties(projectDTO, project);
            project.setBorrowerAnnualRate(configService.getBorrowerAnnualRate());
            project.setAnnualRate(configService.getAnnualRate());
            project.setCommissionAnnualRate(configService.getCommissionAnnualRate());
            project.setIsAssignment(0);

            String projectName = "%s%s第%d次借款";
            String sex = (Integer.parseInt(consumerDTO.getIdNumber().substring(16, 17))) % 2 == 0 ? "女士" : "先生";
            int count = this.count(Wrappers.<Project>lambdaQuery().eq(Project::getConsumerId, consumerDTO.getId()));
            String fullName = consumerDTO.getFullname();
            projectName = String.format(projectName, fullName, sex, count);

            project.setName(projectName);

            this.save(project);

            projectDTO.setId(project.getId());
            projectDTO.setName(projectName);

            return projectDTO;
        }
        throw new BusinessException("未登录");
    }

    @Override
    public PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order, Integer pageNo,
                                            Integer pageSize, String sortBy) {
        QueryWrapper<Project> queryWrapper = Wrappers.<Project>query();

        // 分页
        Page<Project> page = new Page<>(pageNo, pageSize);

        // 排序
        queryWrapper.orderBy((StringUtils.isNotBlank(order) && StringUtils.isBlank(sortBy)), order.toLowerCase().equals("asc"), sortBy);

        // 查询条件
        LambdaQueryWrapper<Project> wrapper = queryWrapper.lambda()
                .ge(projectQueryDTO.getStartAnnualRate() != null, Project::getAnnualRate, projectQueryDTO.getStartAnnualRate())
                .le(projectQueryDTO.getEndAnnualRate() != null, Project::getAnnualRate, projectQueryDTO.getEndAnnualRate())
                .ge(projectQueryDTO.getStartPeriod() != null, Project::getPeriod, projectQueryDTO.getStartPeriod())
                .le(projectQueryDTO.getEndPeriod() != null, Project::getPeriod, projectQueryDTO.getEndPeriod())
                .eq(StringUtils.isNotBlank(projectQueryDTO.getType()), Project::getType, projectQueryDTO.getType())
                .like(StringUtils.isNotBlank(projectQueryDTO.getName()), Project::getName, projectQueryDTO.getName())
                .eq(StringUtils.isNotBlank(projectQueryDTO.getRepaymentWay()), Project::getRepaymentWay, projectQueryDTO.getRepaymentWay())
                .eq(StringUtils.isNotBlank(projectQueryDTO.getProjectStatus()), Project::getProjectStatus, projectQueryDTO.getProjectStatus())
                .eq(projectQueryDTO.getBorrowerAnnualRate() != null, Project::getBorrowerAnnualRate, projectQueryDTO.getBorrowerAnnualRate());

        // 查询后处理返回结果
        IPage<Project> iPage = this.page(page, wrapper);
        List<ProjectDTO> content = iPage.getRecords().stream().map(project -> {
            ProjectDTO projectDTO = new ProjectDTO();
            BeanUtils.copyProperties(project, projectDTO);
            return projectDTO;
        }).collect(Collectors.toList());

        return new PageVO<>(content, iPage.getTotal(), pageNo, pageSize);
    }

    @Override
    public String projectsApprovalStatus(Long id, String approveStatus) {
        Project project = this.getById(id);
        ProjectDTO projectDTO = convertProjectEntityToDTO(project);
        // 生成请求流水号
        projectDTO.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));

        // 调用存管代理服务同步标的信息
        RestResponse<String> restResponse = depositoryAgentFeign.createProject(projectDTO);
        if (restResponse.isSuccessful()) {
            // 修改标的状态为: 已发布
            LambdaUpdateWrapper<Project> updateWrapper = Wrappers.<Project>lambdaUpdate()
                    .eq(Project::getId, id)
                    .set(Project::getStatus, Integer.parseInt(approveStatus));
            boolean update = this.update(updateWrapper);
            if (update) {
                return "success";
            }
        }
        throw new BusinessException(TransactionErrorCode.E_150113);
    }

    @Override
    public List<ProjectDTO> queryProjectsIds(String ids) {
        List<Long> idList = Arrays.asList(ids.split(","))
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>lambdaQuery().in(Project::getId, idList);
        List<Project> projectList = this.list(queryWrapper);
        return projectList.stream()
                .map(this::convertProjectEntityToDTO)
                .map(projectDTO -> {
                    // 查询标的剩余额度
                    BigDecimal projectRemainingAmount = getProjectRemainingAmount(projectDTO);
                    projectDTO.setRemainingAmount(projectRemainingAmount);
                    // 查询标的出借人数
                    Integer tenderCount = tenderMapper.selectCount(
                            Wrappers.<Tender>lambdaQuery()
                                    .eq(Tender::getProjectId, projectDTO.getId())
                    );
                    projectDTO.setTenderCount(tenderCount);
                    return projectDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TenderOverviewDTO> queryTendersByProjectId(Long id) {
        LambdaQueryWrapper<Tender> queryWrapper = Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, id);
        List<Tender> tenderList = tenderMapper.selectList(queryWrapper);
        return tenderList.stream().map(tender -> {
            TenderOverviewDTO tenderOverviewDTO = new TenderOverviewDTO();
            BeanUtils.copyProperties(tender, tenderOverviewDTO);
            // 隐藏手机号中间四位数
            tenderOverviewDTO.setConsumerUsername(CommonUtil.hiddenMobile(tenderOverviewDTO.getConsumerUsername()));
            return tenderOverviewDTO;
        }).collect(Collectors.toList());
    }

    private ProjectDTO convertProjectEntityToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }

    /**
     * 获取标的剩余可投额度
     *
     * @param project
     * @return
     */
    private BigDecimal getProjectRemainingAmount(ProjectDTO project) {
        // 根据标的id在投标表查询已投金额
        List<BigDecimal> decimalList = tenderMapper.selectAmountInvestedByProjectId(project.getId()); // 求和结果集
        BigDecimal amountInvested = new BigDecimal("0.0");
        for (BigDecimal d : decimalList) {
            amountInvested = amountInvested.add(d);
        }
        // 得到剩余额度
        return project.getAmount().subtract(amountInvested);
    }
}
