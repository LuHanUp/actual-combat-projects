package top.luhancc.wanxin.finance.api.depository.agent;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.common.domain.model.repayment.LoanRequest;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.ModifyProjectStatusDTO;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.UserAutoPreTransactionRequest;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;

/**
 * 银行存管系统代理服务API
 *
 * @author luHan
 * @create 2021/6/10 15:36
 * @since 1.0.0
 */
public interface DepositoryAgentApi {

    /**
     * 生成开户请求数据
     *
     * @param consumerRequest 开户信息 * @return
     */
    RestResponse<GatewayRequest> createOpenAccountParam(ConsumerRequest consumerRequest);

    /**
     * 向银行存管系统发送标的信息
     *
     * @param projectDTO
     * @return
     */
    RestResponse<String> createProject(ProjectDTO projectDTO);

    /**
     * 预授权处理
     *
     * @param userAutoPreTransactionRequest 预授权处理信息
     * @return
     */
    RestResponse<String> userAutoPreTransaction(UserAutoPreTransactionRequest
                                                        userAutoPreTransactionRequest);

    /**
     * 审核标的满标放款
     *
     * @param loanRequest
     * @return
     */
    RestResponse<String> confirmLoan(LoanRequest loanRequest);

    /**
     * 修改标的状态
     *
     * @param modifyProjectStatusDTO
     * @return
     */
    RestResponse<String> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO);
}
