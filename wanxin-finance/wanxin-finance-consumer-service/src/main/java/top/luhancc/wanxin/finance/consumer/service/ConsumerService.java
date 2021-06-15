package top.luhancc.wanxin.finance.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryConsumerResponse;
import top.luhancc.wanxin.finance.consumer.mapper.entity.Consumer;

/**
 * @author luHan
 * @create 2021/6/4 14:23
 * @since 1.0.0
 */
public interface ConsumerService extends IService<Consumer> {

    /**
     * 注册用户信息
     *
     * @param consumerRegisterDTO
     * @return
     */
    ConsumerDTO register(ConsumerRegisterDTO consumerRegisterDTO);

    /**
     * 生成开户请求数据
     *
     * @param consumerRequest 开户信息
     * @return
     */
    GatewayRequest createOpenAccountParam(ConsumerRequest consumerRequest);

    boolean modifyResult(DepositoryConsumerResponse response);

    Consumer getByMobile(String mobile);
}
