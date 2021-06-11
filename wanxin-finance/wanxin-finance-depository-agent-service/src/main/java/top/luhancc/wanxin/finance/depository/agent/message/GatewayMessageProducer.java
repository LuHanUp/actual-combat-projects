package top.luhancc.wanxin.finance.depository.agent.message;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryConsumerResponse;

import javax.annotation.Resource;

/**
 * @author luHan
 * @create 2021/6/11 11:18
 * @since 1.0.0
 */
@Component
public class GatewayMessageProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void personalRegister(DepositoryConsumerResponse depositoryConsumerResponse) {
        // 向RocketMQ发送消息
        rocketMQTemplate.convertAndSend("TP_GATEWAY_NOTIFY_AGENT:PERSONAL_REGISTER",
                depositoryConsumerResponse);
    }
}
