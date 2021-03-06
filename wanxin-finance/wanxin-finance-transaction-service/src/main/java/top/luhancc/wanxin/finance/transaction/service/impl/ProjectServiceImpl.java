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
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryReturnCode;
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
import top.luhancc.wanxin.finance.transaction.message.LoansApprovalStatusTransactionalProducer;
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

            String projectName = "%s%s???%d?????????";
            String sex = (Integer.parseInt(consumerDTO.getIdNumber().substring(16, 17))) % 2 == 0 ? "??????" : "??????";
            int count = this.count(Wrappers.<Project>lambdaQuery().eq(Project::getConsumerId, consumerDTO.getId()));
            String fullName = consumerDTO.getFullname();
            projectName = String.format(projectName, fullName, sex, count);

            project.setName(projectName);

            this.save(project);

            projectDTO.setId(project.getId());
            projectDTO.setName(projectName);

            return projectDTO;
        }
        throw new BusinessException("?????????");
    }

    @Override
    public PageVO<ProjectDTO> queryProjects(ProjectQueryDTO projectQueryDTO, String order, Integer pageNo,
                                            Integer pageSize, String sortBy) {
        QueryWrapper<Project> queryWrapper = Wrappers.<Project>query();

        // ??????
        Page<Project> page = new Page<>(pageNo, pageSize);

        // ??????
        queryWrapper.orderBy((StringUtils.isNotBlank(order) && StringUtils.isBlank(sortBy)), order.toLowerCase().equals("asc"), sortBy);

        // ????????????
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

        // ???????????????????????????
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
        // ?????????????????????
        projectDTO.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));

        // ??????????????????????????????????????????
        RestResponse<String> restResponse = depositoryAgentFeign.createProject(projectDTO);
        if (restResponse.isSuccessful() && restResponse.getResult().equalsIgnoreCase(DepositoryReturnCode.RETURN_CODE_00000.getCode())) {
            // ?????????????????????: ?????????
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
                    // ????????????????????????
                    BigDecimal projectRemainingAmount = getProjectRemainingAmount(projectDTO);
                    projectDTO.setRemainingAmount(projectRemainingAmount);
                    // ????????????????????????
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
            // ??????????????????????????????
            tenderOverviewDTO.setConsumerUsername(CommonUtil.hiddenMobile(tenderOverviewDTO.getConsumerUsername()));
            return tenderOverviewDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenderDTO createTender(ProjectInvestDTO projectInvestDTO) {
        // ???????????????????????????????????????????????????
        BigDecimal miniInvestmentAmount = configService.getMiniInvestmentAmount();
        BigDecimal amount = new BigDecimal(projectInvestDTO.getAmount());
        if (amount.compareTo(miniInvestmentAmount) < 0) {
            throw new BusinessException(TransactionErrorCode.E_150109);
        }
        // ??????????????????????????????
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
                // ????????????????????????????????????FULLY????????????
                Project project = this.getById(projectInvestDTO.getId());
                if (ProjectCode.FULLY.getCode().equalsIgnoreCase(project.getProjectStatus())) {
                    throw new BusinessException(TransactionErrorCode.E_150114);
                }
                // ????????????????????????????????????????????????
                ProjectDTO projectDTO = convertProjectEntityToDTO(project);
                BigDecimal remainingAmount = getProjectRemainingAmount(projectDTO);
                if (amount.compareTo(remainingAmount) >= 1) {
                    throw new BusinessException(TransactionErrorCode.E_150110);
                }
                // ????????????????????????????????????????????????????????????????????????
                // ??????????????????1???????????????????????????8??? ??????2??? ????????????1950??? ????????????????????????????????????100??????????????????????????????50??????????????????
                // ????????????????????????????????????
                // ?????????????????????????????????????????? = ???????????????????????? - ??????????????????
                BigDecimal afterRemainingAmount = remainingAmount.subtract(amount);
                if (afterRemainingAmount.compareTo(miniInvestmentAmount) <= -1) {
                    throw new BusinessException(TransactionErrorCode.E_150111);
                }
                // ????????????????????????????????????
                Tender tender = getTender(projectInvestDTO, consumerDTO, project);
                tenderMapper.insert(tender);
                // ???????????????????????????????????????
                RestResponse<String> preTransactionResponse = depositoryAgentFeign.userAutoPreTransaction(
                        getUserAutoPreTransactionRequest(consumerDTO.getUserNo(), amount, project, tender));
                if (preTransactionResponse.isSuccessful() && preTransactionResponse.getResult().equalsIgnoreCase(DepositoryReturnCode.RETURN_CODE_00000.getCode())) {
                    // ?????????????????????????????????
                    tender.setStatus(StatusCode.STATUS_IN.getCode());
                    tenderMapper.updateById(tender);
                    // ????????????????????????
                    remainingAmount = getProjectRemainingAmount(projectDTO);
                    if (remainingAmount.compareTo(new BigDecimal(0)) == 0) {
                        // ??????????????????????????????
                        project.setProjectStatus(ProjectCode.FULLY.getCode());
                        this.updateById(project);
                    }
                    TenderDTO tenderDTO = convertTenderDTO(tender);
                    projectDTO.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getCode());
                    tenderDTO.setProject(projectDTO);
                    // ??????????????????
                    int month = ((Double) Math.ceil(project.getPeriod() / 30.0)).intValue();
                    tenderDTO.setExpectedIncome(
                            IncomeCalcUtil.getIncomeTotalInterest(amount, configService.getAnnualRate(), month));
                    return tenderDTO;
                } else {
                    throw new BusinessException(TransactionErrorCode.E_150115);
                }
            } else {
                throw new BusinessException("????????????????????????,?????????");
            }
        } else {
            throw new BusinessException("????????????????????????");
        }
    }

    @Autowired
    private LoansApprovalStatusTransactionalProducer loansApprovalStatusTransactionalProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loansApprovalStatus(Long id, String approveStatus, String commission) {
        // ??????????????????????????????
        Project project = this.getById(id);
        LambdaQueryWrapper<Tender> queryWrapper = Wrappers.<Tender>lambdaQuery().eq(Tender::getProjectId, id);
        List<Tender> tenderList = tenderMapper.selectList(queryWrapper);
        LoanRequest loanRequest = generateLoanRequest(project, tenderList, commission);
        // ??????????????????--??????????????????,???????????????????????????????????????
        RestResponse<String> restResponse = depositoryAgentFeign.confirmLoan(loanRequest);
        if (!restResponse.isSuccessful() || !restResponse.getResult().equalsIgnoreCase(DepositoryReturnCode.RETURN_CODE_00000.getCode())) {
            throw new BusinessException(TransactionErrorCode.E_150113);
        }
        // ????????????????????????????????????
        updateTenderStatusAlreadyLoan(tenderList);
        // ??????????????????????????????--??????????????????,?????????????????????????????????????????????
        ModifyProjectStatusDTO modifyProjectStatusDTO = new ModifyProjectStatusDTO();
        modifyProjectStatusDTO.setRequestNo(loanRequest.getRequestNo());
        modifyProjectStatusDTO.setProjectNo(project.getProjectNo());
        modifyProjectStatusDTO.setProjectStatus(ProjectCode.REPAYING.getCode());
        modifyProjectStatusDTO.setId(project.getId());
        restResponse = depositoryAgentFeign.modifyProjectStatus(modifyProjectStatusDTO);
        if (!restResponse.isSuccessful() || !restResponse.getResult().equalsIgnoreCase(DepositoryReturnCode.RETURN_CODE_00000.getCode())) {
            throw new BusinessException(TransactionErrorCode.E_150113);
        }
        // ??????????????????????????????,??????RocketMQ?????????????????????????????????,????????????????????????????????????
//        project.setProjectStatus(ProjectCode.REPAYING.getCode());
//        this.updateById(project);
        // ????????????????????????--??????????????????????????????,??????RocketMQ???????????????????????????(???????????????????????????????????????????????????????????????)??????????????????
        ProjectWithTendersDTO projectWithTendersDTO = new ProjectWithTendersDTO();
        projectWithTendersDTO.setProject(convertProjectEntityToDTO(project));
        projectWithTendersDTO.setTenders(convertTenderDTO(tenderList));
        projectWithTendersDTO.setCommissionInvestorAnnualRate(configService.getCommissionInvestorAnnualRate());
        projectWithTendersDTO.setCommissionBorrowerAnnualRate(configService.getCommissionBorrowerAnnualRate());
        // ??????????????????(?????????)???Broker???,RocketMQ??????????????????????????????????????????.jpg?????????1.???
        loansApprovalStatusTransactionalProducer.updateProjectStatusAndStartRepayment(project, projectWithTendersDTO);
        return "ok";
    }

    @Transactional(rollbackFor = BusinessException.class)
    @Override
    public Boolean updateProjectStatusAndStartRepayment(Project project) {
        project.setProjectStatus(ProjectCode.REPAYING.getCode());
        return this.updateById(project);
    }

    /**
     * ??????????????????: ?????????
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
     * ?????????????????????????????????????????????
     *
     * @param project    ????????????
     * @param tenders    ????????????
     * @param commission ??????
     * @return ????????????
     */
    private LoanRequest generateLoanRequest(Project project, List<Tender> tenders, String commission) {
        LoanRequest loanRequest = new LoanRequest();
        // ??????????????????
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
     * ???Tender???????????????TenderDTO??????
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
     * ???Tender???????????????TenderDTO??????
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
     * ?????????????????????????????????
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
        // ????????????
        userAutoPreTransactionRequest.setAmount(amount);
        // ?????????????????????
        userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.TENDER.getCode());
        // ?????????
        userAutoPreTransactionRequest.setProjectNo(project.getProjectNo());
        // ???????????????
        userAutoPreTransactionRequest.setRequestNo(tender.getRequestNo());
        // ?????????????????????
        userAutoPreTransactionRequest.setUserNo(userNo);
        // ?????? ????????????????????????
        userAutoPreTransactionRequest.setId(tender.getId());
        return userAutoPreTransactionRequest;
    }

    /**
     * ????????????????????????
     *
     * @param projectInvestDTO
     * @param consumerDTO
     * @param project
     * @return
     */
    private Tender getTender(ProjectInvestDTO projectInvestDTO, ConsumerDTO consumerDTO,
                             Project project) {
        Tender tender = new Tender();
        // ?????????????????????( ?????????????????? )
        tender.setAmount(new BigDecimal(projectInvestDTO.getAmount()));
        // ?????????????????????
        tender.setConsumerId(consumerDTO.getId());
        tender.setConsumerUsername(consumerDTO.getUsername());
        // ?????????????????????
        tender.setUserNo(consumerDTO.getUserNo());
        // ????????????
        tender.setProjectId(projectInvestDTO.getId());
        // ????????????
        tender.setProjectNo(project.getProjectNo());
        // ????????????
        tender.setTenderStatus(TradingCode.FROZEN.getCode());
        // ????????????
        tender.setCreateDate(LocalDateTime.now());
        // ???????????????
        tender.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        // ????????????
        tender.setStatus(0);
        tender.setProjectName(project.getName());
        // ????????????(??????:???)
        tender.setProjectPeriod(project.getPeriod());
        // ????????????(???????????????)
        tender.setProjectAnnualRate(project.getAnnualRate());
        return tender;
    }

    private ProjectDTO convertProjectEntityToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }

    /**
     * ??????????????????????????????
     *
     * @param project
     * @return
     */
    private BigDecimal getProjectRemainingAmount(ProjectDTO project) {
        // ????????????id??????????????????????????????
        List<BigDecimal> decimalList = tenderMapper.selectAmountInvestedByProjectId(project.getId()); // ???????????????
        BigDecimal amountInvested = new BigDecimal("0.0");
        for (BigDecimal d : decimalList) {
            amountInvested = amountInvested.add(d);
        }
        // ??????????????????
        return project.getAmount().subtract(amountInvested);
    }
}
