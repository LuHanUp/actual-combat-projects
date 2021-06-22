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
import org.springframework.transaction.annotation.Transactional;
import top.luhancc.wanxin.finance.common.domain.*;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.BalanceDetailsDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.ModifyProjectStatusDTO;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.UserAutoPreTransactionRequest;
import top.luhancc.wanxin.finance.common.domain.model.repayment.LoanDetailRequest;
import top.luhancc.wanxin.finance.common.domain.model.repayment.LoanRequest;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.*;
import top.luhancc.wanxin.finance.common.util.CodeNoUtil;
import top.luhancc.wanxin.finance.common.util.CommonUtil;
import top.luhancc.wanxin.finance.transaction.common.constant.ProjectCode;
import top.luhancc.wanxin.finance.transaction.common.constant.RepaymentWayCode;
import top.luhancc.wanxin.finance.transaction.common.constant.TradingCode;
import top.luhancc.wanxin.finance.transaction.common.constant.TransactionErrorCode;
import top.luhancc.wanxin.finance.transaction.common.utils.IncomeCalcUtil;
import top.luhancc.wanxin.finance.transaction.common.utils.SecurityUtil;
import top.luhancc.wanxin.finance.transaction.feign.ConsumerFeign;
import top.luhancc.wanxin.finance.transaction.feign.DepositoryAgentFeign;
import top.luhancc.wanxin.finance.transaction.feign.RepaymentFeign;
import top.luhancc.wanxin.finance.transaction.mapper.ProjectMapper;
import top.luhancc.wanxin.finance.transaction.mapper.TenderMapper;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Tender;
import top.luhancc.wanxin.finance.transaction.service.ConfigService;
import top.luhancc.wanxin.finance.transaction.service.ProjectService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private RepaymentFeign repaymentFeign;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenderDTO createTender(ProjectInvestDTO projectInvestDTO) {
        // 判断投标的金额是否满足最小投标金额
        BigDecimal miniInvestmentAmount = configService.getMiniInvestmentAmount();
        BigDecimal amount = new BigDecimal(projectInvestDTO.getAmount());
        if (amount.compareTo(miniInvestmentAmount) < 0) {
            throw new BusinessException(TransactionErrorCode.E_150109);
        }
        // 判断账户余额是否足够
        RestResponse<ConsumerDTO> restResponse = consumerFeign.getCurrConsumer();
        if (restResponse.isSuccessful()) {
            ConsumerDTO consumerDTO = restResponse.getResult();
            RestResponse<BalanceDetailsDTO> restResponse1 = consumerFeign.getBalance(consumerDTO.getUserNo());
            if (restResponse1.isSuccessful()) {
                BalanceDetailsDTO balanceDetailsDTO = restResponse1.getResult();
                BigDecimal balance = balanceDetailsDTO.getBalance();
                if (balance.compareTo(amount) < 0) {
                    throw new BusinessException(TransactionErrorCode.E_150112);
                }
                // 判断是否满标，标的状态为FULLY即为满标
                Project project = this.getById(projectInvestDTO.getId());
                if (ProjectCode.FULLY.getCode().equalsIgnoreCase(project.getProjectStatus())) {
                    throw new BusinessException(TransactionErrorCode.E_150114);
                }
                // 判断投标金额是否超过剩余未投金额
                ProjectDTO projectDTO = convertProjectEntityToDTO(project);
                BigDecimal remainingAmount = getProjectRemainingAmount(projectDTO);
                if (amount.compareTo(remainingAmount) >= 1) {
                    throw new BusinessException(TransactionErrorCode.E_150110);
                }
                // 判断此次投标后剩余未投金额是否还满足最小投标金额
                // 借款人需要借1万，现在已经投标了8千 还剩2千 本次投标1950元 而系统规定最小投标金额为100元，这就会导致后面的50元无人能投了
                // 所以我们不能出现这种情况
                // 公式：本次投标后剩余未投金额 = 目前剩余未投金额 - 本次投标金额
                BigDecimal afterRemainingAmount = remainingAmount.subtract(amount);
                if (afterRemainingAmount.compareTo(miniInvestmentAmount) <= -1) {
                    throw new BusinessException(TransactionErrorCode.E_150111);
                }
                // 保存投标信息，也就是投标
                Tender tender = getTender(projectInvestDTO, consumerDTO, project);
                tenderMapper.insert(tender);
                // 向存管代理服务发送投标请求
                RestResponse<String> preTransactionResponse = depositoryAgentFeign.userAutoPreTransaction(
                        getUserAutoPreTransactionRequest(consumerDTO.getUserNo(), amount, project, tender));
                if (preTransactionResponse.isSuccessful()) {
                    // 更新投标状态，为已同步
                    tender.setStatus(StatusCode.STATUS_IN.getCode());
                    tenderMapper.updateById(tender);
                    // 判断是否已经满标
                    remainingAmount = getProjectRemainingAmount(projectDTO);
                    if (remainingAmount.compareTo(new BigDecimal(0)) == 0) {
                        // 修改标的状态为已满标
                        project.setProjectStatus(ProjectCode.FULLY.getCode());
                        this.updateById(project);
                    }
                    TenderDTO tenderDTO = convertTenderDTO(tender);
                    projectDTO.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getCode());
                    tenderDTO.setProject(projectDTO);
                    // 设置预期收益
                    int month = ((Double) Math.ceil(project.getPeriod() / 30.0)).intValue();
                    tenderDTO.setExpectedIncome(
                            IncomeCalcUtil.getIncomeTotalInterest(amount, configService.getAnnualRate(), month));
                    return tenderDTO;
                } else {
                    throw new BusinessException(TransactionErrorCode.E_150115);
                }
            } else {
                throw new BusinessException("获取用户余额失败,请重试");
            }
        } else {
            throw new BusinessException("请登录后进行投标");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loansApprovalStatus(Long id, String approveStatus, String commission) {
        // 阶段一：生成放款明细
        Project project = this.getById(id);
        LambdaQueryWrapper<Tender> queryWrapper = Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, id);
        List<Tender> tenderList = tenderMapper.selectList(queryWrapper);
        LoanRequest loanRequest = generateLoanRequest(project, tenderList, commission);
        // 阶段二：放款--存管代理服务,让银行存款系统那边进行放款
        RestResponse<String> restResponse = depositoryAgentFeign.confirmLoan(loanRequest);
        if (!restResponse.isSuccessful()) {
            throw new BusinessException(TransactionErrorCode.E_150113);
        }
        // 修改投标信息状态为已放款
        updateTenderStatusAlreadyLoan(tenderList);
        // 阶段三：修改标的状态--存管代理服务,修改银行存款系统那边的标的状态
        ModifyProjectStatusDTO modifyProjectStatusDTO = new ModifyProjectStatusDTO();
        modifyProjectStatusDTO.setRequestNo(loanRequest.getRequestNo());
        modifyProjectStatusDTO.setProjectNo(project.getProjectNo());
        modifyProjectStatusDTO.setProjectStatus(ProjectCode.REPAYING.getCode());
        modifyProjectStatusDTO.setId(project.getId());
        restResponse = depositoryAgentFeign.modifyProjectStatus(modifyProjectStatusDTO);
        if (!restResponse.isSuccessful()) {
            throw new BusinessException(TransactionErrorCode.E_150113);
        }
        // 修改标的状态为还款中
        project.setProjectStatus(ProjectCode.REPAYING.getCode());
        this.updateById(project);
        // 阶段四：启动还款--还款服务生成还款计划
        ProjectWithTendersDTO projectWithTendersDTO = new ProjectWithTendersDTO();
        projectWithTendersDTO.setProject(convertProjectEntityToDTO(project));
        projectWithTendersDTO.setTenders(convertTenderDTO(tenderList));
        projectWithTendersDTO.setCommissionInvestorAnnualRate(configService.getCommissionInvestorAnnualRate());
        projectWithTendersDTO.setCommissionBorrowerAnnualRate(configService.getCommissionBorrowerAnnualRate());
        return "ok";
    }

    /**
     * 更新投标信息: 已放款
     *
     * @param tenderList
     */
    private void updateTenderStatusAlreadyLoan(List<Tender> tenderList) {
        for (Tender tender : tenderList) {
            tender.setTenderStatus(TradingCode.LOAN.getCode());
            tenderMapper.updateById(tender);
        }
    }

    /**
     * 根据标的和投标信息生成放款信息
     *
     * @param project    标的信息
     * @param tenders    投标信息
     * @param commission 佣金
     * @return 放款信息
     */
    private LoanRequest generateLoanRequest(Project project, List<Tender> tenders, String commission) {
        LoanRequest loanRequest = new LoanRequest();
        // 处理投标明细
        List<LoanDetailRequest> details = new ArrayList<>();
        for (Tender tender : tenders) {
            LoanDetailRequest loanDetailRequest = new LoanDetailRequest();
            loanDetailRequest.setAmount(tender.getAmount());
            loanDetailRequest.setPreRequestNo(tender.getRequestNo());
            details.add(loanDetailRequest);
        }
        loanRequest.setDetails(details);
        loanRequest.setCommission(StringUtils.isBlank(commission) ? new BigDecimal(0) : new BigDecimal(commission));
        loanRequest.setProjectNo(project.getProjectNo());
        loanRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        loanRequest.setId(project.getId());
        return loanRequest;
    }

    /**
     * 将Tender对象转换为TenderDTO对象
     *
     * @param tender
     * @return
     */
    private TenderDTO convertTenderDTO(Tender tender) {
        TenderDTO tenderDTO = new TenderDTO();
        BeanUtils.copyProperties(tender, tenderDTO);
        return tenderDTO;
    }

    /**
     * 将Tender对象转换为TenderDTO对象
     *
     * @param tenders
     * @return
     */
    private List<TenderDTO> convertTenderDTO(List<Tender> tenders) {
        List<TenderDTO> tenderDTOS = new ArrayList<>();
        for (Tender tender : tenders) {
            tenderDTOS.add(convertTenderDTO(tender));
        }
        return tenderDTOS;
    }

    /**
     * 获取预授权处理请求信息
     *
     * @param userNo
     * @param amount
     * @param project
     * @param tender
     * @return
     */
    private UserAutoPreTransactionRequest getUserAutoPreTransactionRequest(String userNo, BigDecimal amount,
                                                                           Project project, Tender tender) {
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = new UserAutoPreTransactionRequest();
        // 冻结金额
        userAutoPreTransactionRequest.setAmount(amount);
        // 预处理业务类型
        userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.TENDER.getCode());
        // 标的号
        userAutoPreTransactionRequest.setProjectNo(project.getProjectNo());
        // 请求流水号
        userAutoPreTransactionRequest.setRequestNo(tender.getRequestNo());
        // 投资人用户编码
        userAutoPreTransactionRequest.setUserNo(userNo);
        // 设置 关联业务实体标识
        userAutoPreTransactionRequest.setId(tender.getId());
        return userAutoPreTransactionRequest;
    }

    /**
     * 生成投标信息数据
     *
     * @param projectInvestDTO
     * @param consumerDTO
     * @param project
     * @return
     */
    private Tender getTender(ProjectInvestDTO projectInvestDTO, ConsumerDTO consumerDTO,
                             Project project) {
        Tender tender = new Tender();
        // 投资人投标金额( 投标冻结金额 )
        tender.setAmount(new BigDecimal(projectInvestDTO.getAmount()));
        // 投标人用户标识
        tender.setConsumerId(consumerDTO.getId());
        tender.setConsumerUsername(consumerDTO.getUsername());
        // 投标人用户编码
        tender.setUserNo(consumerDTO.getUserNo());
        // 标的标识
        tender.setProjectId(projectInvestDTO.getId());
        // 标的编码
        tender.setProjectNo(project.getProjectNo());
        // 投标状态
        tender.setTenderStatus(TradingCode.FROZEN.getCode());
        // 创建时间
        tender.setCreateDate(LocalDateTime.now());
        // 请求流水号
        tender.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        // 可用状态
        tender.setStatus(0);
        tender.setProjectName(project.getName());
        // 标的期限(单位:天)
        tender.setProjectPeriod(project.getPeriod());
        // 年化利率(投资人视图)
        tender.setProjectAnnualRate(project.getAnnualRate());
        return tender;
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
