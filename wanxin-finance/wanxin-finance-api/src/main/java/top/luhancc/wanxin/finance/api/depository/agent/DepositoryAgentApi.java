package top.luhancc.wanxin.finance.api.depository.agent;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;

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
}
