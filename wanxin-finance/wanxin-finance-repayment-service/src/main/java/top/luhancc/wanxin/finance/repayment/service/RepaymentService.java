package top.luhancc.wanxin.finance.repayment.service;

import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;

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
}
