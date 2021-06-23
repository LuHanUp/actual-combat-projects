package top.luhancc.wanxin.finance.repayment.service;

import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
import top.luhancc.wanxin.finance.common.domain.model.repayment.RepaymentRequest;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentDetail;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentPlan;

import java.util.List;

/**
 * @author luHan
 * @create 2021/6/22 17:07
 * @since 1.0.0
 */
public interface RepaymentService {

    /**
     * 启动还款
     *
     * @param projectWithTendersDTO
     * @return
     */
    String startRepayment(ProjectWithTendersDTO projectWithTendersDTO);

    /**
     * 通过标的id和借款人id查询还款计划记录数
     *
     * @param consumerId 借款人id
     * @param projectId  标的id
     * @return
     */
    int getRepaymentCountByProjectIdAndConsumerId(Long consumerId, Long projectId);

    /**
     * 查询到期还款计划
     *
     * @param date 格式为:yyyy-MM-dd
     * @return
     */
    List<RepaymentPlan> selectDueRepayment(String date);

    /**
     * 根据还款计划生成还款明细并保存
     *
     * @param repaymentPlan
     * @return
     */
    RepaymentDetail saveRepaymentDetail(RepaymentPlan repaymentPlan);

    /**
     * 还款预处理:冻结借款人应还金额
     *
     * @param repaymentPlan
     * @param preRequestNo
     * @return
     */
    Boolean preRepayment(RepaymentPlan repaymentPlan, String preRequestNo);

    /**
     * 确认还款处理
     * <p>
     * 1)更新还款明细为:已同步
     * 2)更新应收明细状态为: 已收
     * 3)更新还款计划状态:已还款
     *
     * @param repaymentPlan
     * @param repaymentRequest
     * @return
     */
    Boolean confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest);

    /**
     * 远程调用确认还款接口
     *
     * @param repaymentPlan
     * @param repaymentRequest
     */
    void invokeConfirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest);

    RepaymentPlan getByRepaymentPlanId(Long id);
}
