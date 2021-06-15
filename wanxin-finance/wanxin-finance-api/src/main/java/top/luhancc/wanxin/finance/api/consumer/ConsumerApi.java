package top.luhancc.wanxin.finance.api.consumer;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;

/**
 * 用户api
 *
 * @author luHan
 * @create 2021/6/4 11:41
 * @since 1.0.0
 */
public interface ConsumerApi {

    /**
     * 用户注册，保存用户信息
     *
     * @param consumerRegisterDTO
     * @return
     */
    RestResponse<ConsumerDTO> register(ConsumerRegisterDTO consumerRegisterDTO);

    /**
     * 生成开户请求数据
     *
     * @param consumerRequest 开户信息
     * @return
     */
    RestResponse<GatewayRequest> createOpenAccountParam(ConsumerRequest consumerRequest);

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    RestResponse<ConsumerDTO> getCurrConsumer();
}
