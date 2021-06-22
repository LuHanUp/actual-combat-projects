package top.luhancc.wanxin.finance.api.repayment;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;

/**
 * @author luHan
 * @create 2021/6/22 15:36
 * @since 1.0.0
 */
public interface RepaymentApi {

    /**
     * 启动还款
     *
     * @param projectWithTendersDTO
     * @return
     */
    public RestResponse<String> startRepayment(ProjectWithTendersDTO projectWithTendersDTO);
}
