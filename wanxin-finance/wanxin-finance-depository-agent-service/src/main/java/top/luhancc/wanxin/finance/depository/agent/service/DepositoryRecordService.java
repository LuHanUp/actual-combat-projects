package top.luhancc.wanxin.finance.depository.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryBaseResponse;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryResponseDTO;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.UserAutoPreTransactionRequest;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.depository.agent.mapper.entity.DepositoryRecord;

/**
 * @author luHan
 * @create 2021/6/10 16:57
 * @since 1.0.0
 */
public interface DepositoryRecordService extends IService<DepositoryRecord> {

    /**
     * 生成开户请求数据
     * <p>
     * 保存交易记录
     * 对数据进行签名加密后返回
     *
     * @param consumerRequest 开户信息
     * @return
     */
    GatewayRequest createOpenAccountParam(ConsumerRequest consumerRequest);

    /**
     * 修改请求的状态
     *
     * @param requestNo 请求编号
     * @param status    状态
     * @return
     */
    boolean modifyRequestStatus(String requestNo, Integer status);

    /**
     * 保存标的
     *
     * @param projectDTO
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> createProject(ProjectDTO projectDTO);

    /**
     * 投标预处理
     *
     * @param userAutoPreTransactionRequest
     * @return
     */
    DepositoryResponseDTO<DepositoryBaseResponse> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest);
}
