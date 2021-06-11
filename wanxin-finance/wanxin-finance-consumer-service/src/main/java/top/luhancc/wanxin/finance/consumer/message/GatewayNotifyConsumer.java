package top.luhancc.wanxin.finance.consumer.message;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryConsumerResponse;
import top.luhancc.wanxin.finance.consumer.service.ConsumerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 接收RocketMQ存款代理服务发送的通知消息
 *
 * @author luHan
 * @create 2021/6/11 13:39
 * @since 1.0.0
 */
@Component
public class GatewayNotifyConsumer {
    @Autowired
    private ConsumerService consumerService;

    private String topic = "TP_GATEWAY_NOTIFY_AGENT";

    public GatewayNotifyConsumer(@Value("${rocketmq.consumer.group}") String consumerGroup,
                                 @Value("${rocketmq.name-server}") String namesServerAddr) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.setNamesrvAddr(namesServerAddr);
        // 从最后一个开始消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        try {
            defaultMQPushConsumer.subscribe(topic, "*");

            // 注册消息消费监听器
            defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    if (!CollectionUtils.isEmpty(msgs)) {
                        try {
                            MessageExt messageExt = msgs.get(0);
                            String topic = messageExt.getTopic();
                            String tag = messageExt.getTags();
                            String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                            if ("PERSONAL_REGISTER".equalsIgnoreCase(tag)) {
                                DepositoryConsumerResponse depositoryConsumerResponse = JSON.parseObject(messageBody, DepositoryConsumerResponse.class);
                                // 更新开户信息
                                consumerService.modifyResult(depositoryConsumerResponse);
                            }
                        } catch (Exception e) {
                            // 消费失败，稍后重试
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            defaultMQPushConsumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}
