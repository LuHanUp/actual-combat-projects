package top.luhancc.wanxin.finance.repayment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import freemarker.core.BugException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.luhancc.wanxin.finance.common.domain.*;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryReturnCode;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.UserAutoPreTransactionRequest;
import top.luhancc.wanxin.finance.common.domain.model.repayment.EqualInterestRepayment;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
import top.luhancc.wanxin.finance.common.domain.model.repayment.RepaymentRequest;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.TenderDTO;
import top.luhancc.wanxin.finance.common.util.CodeNoUtil;
import top.luhancc.wanxin.finance.common.util.DateUtil;
import top.luhancc.wanxin.finance.repayment.common.utils.RepaymentUtil;
import top.luhancc.wanxin.finance.repayment.feign.DepositoryAgentFeign;
import top.luhancc.wanxin.finance.repayment.mapper.ReceivableDetailMapper;
import top.luhancc.wanxin.finance.repayment.mapper.ReceivablePlanMapper;
import top.luhancc.wanxin.finance.repayment.mapper.RepaymentDetailMapper;
import top.luhancc.wanxin.finance.repayment.mapper.RepaymentPlanMapper;
import top.luhancc.wanxin.finance.repayment.mapper.entity.ReceivableDetail;
import top.luhancc.wanxin.finance.repayment.mapper.entity.ReceivablePlan;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentDetail;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentPlan;
import top.luhancc.wanxin.finance.repayment.service.RepaymentService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luHan
 * @create 2021/6/22 17:07
 * @since 1.0.0
 */
@Service
public class RepaymentServiceImpl implements RepaymentService {
    @Resource
    private ReceivablePlanMapper receivablePlanMapper;
    @Resource
    private RepaymentPlanMapper repaymentPlanMapper;
    @Resource
    private RepaymentDetailMapper repaymentDetailMapper;
    @Resource
    private ReceivableDetailMapper receivableDetailMapper;
    @Autowired
    private DepositoryAgentFeign depositoryAgentFeign;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startRepayment(ProjectWithTendersDTO projectWithTendersDTO) {
        // 生成借款人还款计划
        ProjectDTO projectDTO = projectWithTendersDTO.getProject();
        List<TenderDTO> tenderDTOList = projectWithTendersDTO.getTenders();
        // 计算还款月数
        int month = ((Double) Math.ceil(projectDTO.getPeriod() / 30.0)).intValue();
        String repaymentWay = projectDTO.getRepaymentWay();// 还款方式，目前只针对等额本息
        if (RepaymentWayCode.FIXED_REPAYMENT.getCode().equalsIgnoreCase(repaymentWay)) {
            EqualInterestRepayment fixedRepayment = RepaymentUtil.fixedRepayment(projectDTO.getAmount(),
                    projectDTO.getBorrowerAnnualRate(), month, projectDTO.getCommissionAnnualRate());
            List<RepaymentPlan> repaymentPlans = saveRepaymentPlan(projectDTO, fixedRepayment);
            // 生成投资人收资明细
            for (TenderDTO tenderDTO : tenderDTOList) {
                EqualInterestRepayment receiptRepayment = RepaymentUtil.fixedRepayment(tenderDTO.getAmount(), tenderDTO.getProjectAnnualRate(),
                        month, projectWithTendersDTO.getCommissionInvestorAnnualRate());
                /* 由于投标人的收款明细需要还款信息,所以遍历还款计划, 把还款期数与投资人应收期数对应上*/
                repaymentPlans.forEach(plan -> {
                    // 2.2 保存应收明细到数据库
                    saveReceivablePlan(plan, tenderDTO, receiptRepayment);
                });
            }
            return DepositoryReturnCode.RETURN_CODE_00000.getCode();
        } else {
            return DepositoryReturnCode.RETURN_CODE_00002.getCode();
        }
    }

    @Override
    public int getRepaymentCountByProjectIdAndConsumerId(Long consumerId, Long projectId) {
        LambdaQueryWrapper<RepaymentPlan> queryWrapper = Wrappers.<RepaymentPlan>lambdaQuery()
                .eq(RepaymentPlan::getConsumerId, consumerId)
                .eq(RepaymentPlan::getProjectId, projectId);
        return repaymentPlanMapper.selectCount(queryWrapper);
    }

    @Override
    public List<RepaymentPlan> selectDueRepayment(String date) {
        return repaymentPlanMapper.selectDueRepayment(date);
    }

    @Override
    public RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan) {
        LambdaQueryWrapper<RepaymentDetail> queryWrapper = Wrappers.<RepaymentDetail>lambdaQuery()
                .eq(RepaymentDetail::getRepaymentPlanId, repaymentPlan.getId());
        RepaymentDetail repaymentDetail = repaymentDetailMapper.selectOne(queryWrapper);
        if (repaymentDetail == null) {
            repaymentDetail = new RepaymentDetail();
            repaymentDetail.setAmount(repaymentPlan.getAmount());
            repaymentDetail.setRepaymentDate(LocalDateTime.now());
            repaymentDetail.setRepaymentPlanId(repaymentPlan.getId());
            repaymentDetail.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
            repaymentDetail.setStatus(StatusCode.STATUS_OUT.getCode());
            repaymentDetailMapper.insert(repaymentDetail);
        }
        return repaymentDetail;
    }

    @Override
    public Boolean preRepayment(RepaymentPlan repaymentPlan, String preRequestNo) {
        UserAutoPreTransactionRequest userAutoPreTransactionRequest = new UserAutoPreTransactionRequest();
        userAutoPreTransactionRequest.setAmount(repaymentPlan.getAmount());
        userAutoPreTransactionRequest.setRequestNo(preRequestNo);
        userAutoPreTransactionRequest.setUserNo(repaymentPlan.getUserNo());
        userAutoPreTransactionRequest.setBizType(PreprocessBusinessTypeCode.REPAYMENT.getCode());
        userAutoPreTransactionRequest.setPreMarketingAmount(new BigDecimal(0));
        userAutoPreTransactionRequest.setRemark("还款冻结资金");
        userAutoPreTransactionRequest.setProjectNo(repaymentPlan.getProjectNo());
        userAutoPreTransactionRequest.setId(repaymentPlan.getId());
        RestResponse<String> restResponse = depositoryAgentFeign.userAutoPreTransaction(userAutoPreTransactionRequest);
        if (restResponse.isSuccessful()
                && DepositoryReturnCode.RETURN_CODE_00000.getCode().equalsIgnoreCase(restResponse.getResult())) {
            return true;
        }
        throw new BugException(restResponse.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest) {
        // 设置还款明细为已同步
        String preRequestNo = repaymentRequest.getPreRequestNo();
        repaymentDetailMapper.update(null, Wrappers.<RepaymentDetail>lambdaUpdate()
                .set(RepaymentDetail::getStatus, StatusCode.STATUS_IN.getCode())
                .eq(RepaymentDetail::getRequestNo, preRequestNo));
        // 投资人应收明细为已收
        List<ReceivablePlan> receivablePlanList = receivablePlanMapper.selectList(Wrappers.<ReceivablePlan>lambdaUpdate()
                .eq(ReceivablePlan::getRepaymentId, repaymentPlan.getId()));
        receivablePlanList.forEach(receivablePlan -> {
            receivablePlan.setReceivableStatus(1);
            receivablePlanMapper.updateById(receivablePlan);
            // 保存应收明细
            ReceivableDetail receivableDetail = new ReceivableDetail();
            receivableDetail.setReceivableId(receivablePlan.getId());
            receivableDetail.setAmount(receivablePlan.getAmount());
            receivableDetail.setReceivableDate(LocalDateTime.now());
            receivableDetailMapper.insert(receivableDetail);
        });
        // 更新还款计划为：已还款
        repaymentPlan.setRepaymentStatus("1");
        repaymentPlanMapper.updateById(repaymentPlan);
        return true;
    }

    /**
     * 保存还款计划到数据库
     *
     * @param projectDTO     标的信息
     * @param fixedRepayment 等额本息还款明细
     * @return
     */
    private List<RepaymentPlan> saveRepaymentPlan(ProjectDTO projectDTO, EqualInterestRepayment fixedRepayment) {
        List<RepaymentPlan> repaymentPlans = new ArrayList<>();
        // 获取每期利息
        Map<Integer, BigDecimal> interestMap = fixedRepayment.getInterestMap();
        // 平台收取利息
        Map<Integer, BigDecimal> commissionMap = fixedRepayment.getCommissionMap();
        fixedRepayment.getPrincipalMap().forEach((k, v) -> {
            RepaymentPlan repaymentPlan = new RepaymentPlan();
            repaymentPlan.setConsumerId(projectDTO.getConsumerId());
            repaymentPlan.setUserNo(projectDTO.getUserNo());
            repaymentPlan.setProjectId(projectDTO.getId());
            repaymentPlan.setProjectNo(projectDTO.getProjectNo());
            repaymentPlan.setNumberOfPeriods(k);
            repaymentPlan.setInterest(interestMap.get(k));
            repaymentPlan.setPrincipal(v);
            repaymentPlan.setAmount(repaymentPlan.getPrincipal().add(repaymentPlan.getInterest()));
            repaymentPlan.setShouldRepaymentDate(DateUtil.localDateTimeAddMonth(LocalDateTime.now(), k));
            repaymentPlan.setRepaymentStatus("0");
            repaymentPlan.setCreateDate(LocalDateTime.now());
            repaymentPlan.setCommission(commissionMap.get(k));

            repaymentPlanMapper.insert(repaymentPlan);
            repaymentPlans.add(repaymentPlan);
        });
        return repaymentPlans;
    }

    /**
     * 保存应收明细到数据库
     *
     * @param repaymentPlan    还款计划
     * @param tender           投标信息
     * @param receiptRepayment
     */
    private void saveReceivablePlan(RepaymentPlan repaymentPlan,
                                    TenderDTO tender,
                                    EqualInterestRepayment receiptRepayment) {
        // 应收本金
        Map<Integer, BigDecimal> principalMap = receiptRepayment.getPrincipalMap(); // 应收利息
        Map<Integer, BigDecimal> interestMap = receiptRepayment.getInterestMap(); // 平台收取利息
        Map<Integer, BigDecimal> commissionMap = receiptRepayment.getCommissionMap(); // 封装投资人应收明细
        ReceivablePlan receivablePlan = new ReceivablePlan();
        // 投标信息标识
        receivablePlan.setTenderId(tender.getId());
        // 设置期数
        receivablePlan.setNumberOfPeriods(repaymentPlan.getNumberOfPeriods());
        // 投标人用户标识
        receivablePlan.setConsumerId(tender.getConsumerId());
        // 投标人用户编码
        receivablePlan.setUserNo(tender.getUserNo());
        // 还款计划项标识
        receivablePlan.setRepaymentId(repaymentPlan.getId());
        // 应收利息
        receivablePlan.setInterest(interestMap.get(repaymentPlan.getNumberOfPeriods())); // 应收本金
        receivablePlan.setPrincipal(principalMap.get(repaymentPlan.getNumberOfPeriods()));
        // 应收本息 = 应收本金 + 应收利息
        receivablePlan.setAmount(receivablePlan.getInterest().add(receivablePlan.getPrincipal()));
        // 应收时间
        receivablePlan.setShouldReceivableDate(repaymentPlan.getShouldRepaymentDate());
        // 应收状态, 当前业务为未收
        receivablePlan.setReceivableStatus(0);
        // 创建时间
        receivablePlan.setCreateDate(DateUtil.now());
        // 设置投资人让利, 注意这个地方是具体: 佣金
        receivablePlan.setCommission(commissionMap.get(repaymentPlan.getNumberOfPeriods()));
        receivablePlanMapper.insert(receivablePlan);
    }
}
