package top.luhancc.wanxin.finance.api.consumer;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;

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
}
