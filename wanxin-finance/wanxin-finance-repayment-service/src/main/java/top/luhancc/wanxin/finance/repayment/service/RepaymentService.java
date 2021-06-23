package top.luhancc.wanxin.finance.repayment.service;

import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
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
}
