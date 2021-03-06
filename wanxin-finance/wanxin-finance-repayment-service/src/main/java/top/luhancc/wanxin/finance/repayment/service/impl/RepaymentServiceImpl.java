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
import top.luhancc.wanxin.finance.common.domain.model.repayment.RepaymentDetailRequest;
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
import top.luhancc.wanxin.finance.repayment.message.RepaymentMessageProducer;
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
    @Autowired
    private RepaymentMessageProducer repaymentMessageProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startRepayment(ProjectWithTendersDTO projectWithTendersDTO) {
        // ???????????????????????????
        ProjectDTO projectDTO = projectWithTendersDTO.getProject();
        List<TenderDTO> tenderDTOList = projectWithTendersDTO.getTenders();
        // ??????????????????
        int month = ((Double) Math.ceil(projectDTO.getPeriod() / 30.0)).intValue();
        String repaymentWay = projectDTO.getRepaymentWay();// ??????????????????????????????????????????
        if (RepaymentWayCode.FIXED_REPAYMENT.getCode().equalsIgnoreCase(repaymentWay)) {
            EqualInterestRepayment fixedRepayment = RepaymentUtil.fixedRepayment(projectDTO.getAmount(),
                    projectDTO.getBorrowerAnnualRate(), month, projectDTO.getCommissionAnnualRate());
            List<RepaymentPlan> repaymentPlans = saveRepaymentPlan(projectDTO, fixedRepayment);
            // ???????????????????????????
            for (TenderDTO tenderDTO : tenderDTOList) {
                EqualInterestRepayment receiptRepayment = RepaymentUtil.fixedRepayment(tenderDTO.getAmount(), tenderDTO.getProjectAnnualRate(),
                        month, projectWithTendersDTO.getCommissionInvestorAnnualRate());
                /* ????????????????????????????????????????????????,????????????????????????, ????????????????????????????????????????????????*/
                repaymentPlans.forEach(plan -> {
                    // 2.2 ??????????????????????????????
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
    public void executeRepayment(String date, int shardingTotal, int shardingItem) {
        //???????????????????????????
        List<RepaymentPlan> repaymentPlanList = selectDueRepayment(date, shardingTotal, shardingItem);
        //??????????????????
        repaymentPlanList.forEach(repaymentPlan -> {
            RepaymentDetail repaymentDetail = saveRepaymentDetail(repaymentPlan);
            System.out.println("???????????????" + shardingItem + "\n" + repaymentPlan);
            //???????????????
            Boolean proRepaymentResult = preRepayment(repaymentPlan, repaymentDetail.getRequestNo());

            if (proRepaymentResult) {
                System.out.println("?????????????????????");
                String preRequestNo = repaymentDetail.getRequestNo();
                RepaymentRequest repaymentRequest = generateRepaymentRequest(repaymentPlan, preRequestNo);
                repaymentMessageProducer.confirmRepayment(repaymentPlan, repaymentRequest);
            }
        });
    }

    @Override
    public List<RepaymentPlan> selectDueRepayment(String date, int shardingTotal, int shardingItem) {
        return repaymentPlanMapper.selectDueRepaymentSharding(date, shardingTotal, shardingItem);
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
        userAutoPreTransactionRequest.setRemark("??????????????????");
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
        // ??????????????????????????????
        String preRequestNo = repaymentRequest.getPreRequestNo();
        repaymentDetailMapper.update(null, Wrappers.<RepaymentDetail>lambdaUpdate()
                .set(RepaymentDetail::getStatus, StatusCode.STATUS_IN.getCode())
                .eq(RepaymentDetail::getRequestNo, preRequestNo));
        // ??????????????????????????????
        List<ReceivablePlan> receivablePlanList = receivablePlanMapper.selectList(Wrappers.<ReceivablePlan>lambdaUpdate()
                .eq(ReceivablePlan::getRepaymentId, repaymentPlan.getId()));
        receivablePlanList.forEach(receivablePlan -> {
            receivablePlan.setReceivableStatus(1);
            receivablePlanMapper.updateById(receivablePlan);
            // ??????????????????
            ReceivableDetail receivableDetail = new ReceivableDetail();
            receivableDetail.setReceivableId(receivablePlan.getId());
            receivableDetail.setAmount(receivablePlan.getAmount());
            receivableDetail.setReceivableDate(LocalDateTime.now());
            receivableDetailMapper.insert(receivableDetail);
        });
        // ?????????????????????????????????
        repaymentPlan.setRepaymentStatus("1");
        repaymentPlanMapper.updateById(repaymentPlan);
        return true;
    }

    @Override
    public void invokeConfirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest) {
        RestResponse<String> repaymentResponse = depositoryAgentFeign.confirmRepayment(repaymentRequest);
        if (!repaymentResponse.isSuccessful() ||
                !DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(repaymentResponse.getResult())) {
            throw new BusinessException("????????????");
        }
    }

    @Override
    public RepaymentPlan getByRepaymentPlanId(Long id) {
        return repaymentPlanMapper.selectById(id);
    }

    /**
     * ??????????????????????????????
     *
     * @param projectDTO     ????????????
     * @param fixedRepayment ????????????????????????
     * @return
     */
    private List<RepaymentPlan> saveRepaymentPlan(ProjectDTO projectDTO, EqualInterestRepayment fixedRepayment) {
        List<RepaymentPlan> repaymentPlans = new ArrayList<>();
        // ??????????????????
        Map<Integer, BigDecimal> interestMap = fixedRepayment.getInterestMap();
        // ??????????????????
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
     * ??????????????????????????????
     *
     * @param repaymentPlan    ????????????
     * @param tender           ????????????
     * @param receiptRepayment
     */
    private void saveReceivablePlan(RepaymentPlan repaymentPlan,
                                    TenderDTO tender,
                                    EqualInterestRepayment receiptRepayment) {
        // ????????????
        Map<Integer, BigDecimal> principalMap = receiptRepayment.getPrincipalMap(); // ????????????
        Map<Integer, BigDecimal> interestMap = receiptRepayment.getInterestMap(); // ??????????????????
        Map<Integer, BigDecimal> commissionMap = receiptRepayment.getCommissionMap(); // ???????????????????????????
        ReceivablePlan receivablePlan = new ReceivablePlan();
        // ??????????????????
        receivablePlan.setTenderId(tender.getId());
        // ????????????
        receivablePlan.setNumberOfPeriods(repaymentPlan.getNumberOfPeriods());
        // ?????????????????????
        receivablePlan.setConsumerId(tender.getConsumerId());
        // ?????????????????????
        receivablePlan.setUserNo(tender.getUserNo());
        // ?????????????????????
        receivablePlan.setRepaymentId(repaymentPlan.getId());
        // ????????????
        receivablePlan.setInterest(interestMap.get(repaymentPlan.getNumberOfPeriods())); // ????????????
        receivablePlan.setPrincipal(principalMap.get(repaymentPlan.getNumberOfPeriods()));
        // ???????????? = ???????????? + ????????????
        receivablePlan.setAmount(receivablePlan.getInterest().add(receivablePlan.getPrincipal()));
        // ????????????
        receivablePlan.setShouldReceivableDate(repaymentPlan.getShouldRepaymentDate());
        // ????????????, ?????????????????????
        receivablePlan.setReceivableStatus(0);
        // ????????????
        receivablePlan.setCreateDate(DateUtil.now());
        // ?????????????????????, ???????????????????????????: ??????
        receivablePlan.setCommission(commissionMap.get(repaymentPlan.getNumberOfPeriods()));
        receivablePlanMapper.insert(receivablePlan);
    }

    private RepaymentRequest generateRepaymentRequest(RepaymentPlan repaymentPlan, String preRequestNo) {
        //??????????????????id,??????????????????
        List<ReceivablePlan> receivablePlanList = receivablePlanMapper.selectList(Wrappers.<ReceivablePlan>lambdaQuery().eq(ReceivablePlan::getRepaymentId, repaymentPlan.getId()));
        RepaymentRequest repaymentRequest = new RepaymentRequest();
        // ????????????
        repaymentRequest.setAmount(repaymentPlan.getAmount());
        // ????????????id
        repaymentRequest.setId(repaymentPlan.getId());
        // ???????????????????????????
        repaymentRequest.setCommission(repaymentPlan.getCommission());
        // ????????????
        repaymentRequest.setProjectNo(repaymentPlan.getProjectNo());
        // ???????????????
        repaymentRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        // ????????????????????????
        repaymentRequest.setPreRequestNo(preRequestNo);
        List<RepaymentDetailRequest> detailRequests = new ArrayList<>();
        receivablePlanList.forEach(receivablePlan -> {
            RepaymentDetailRequest detailRequest = new RepaymentDetailRequest();
            // ?????????????????????
            detailRequest.setUserNo(receivablePlan.getUserNo());
            // ???????????????????????????
            detailRequest.setCommission(receivablePlan.getCommission());
            // ?????????????????????
            detailRequest.setAmount(receivablePlan.getPrincipal());
            // ?????????????????????
            detailRequest.setInterest(receivablePlan.getInterest());
            //???????????????
            detailRequests.add(detailRequest);
        });
        repaymentRequest.setDetails(detailRequests);
        return repaymentRequest;
    }
}
