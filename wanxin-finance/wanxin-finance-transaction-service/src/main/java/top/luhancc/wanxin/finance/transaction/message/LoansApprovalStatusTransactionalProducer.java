package top.luhancc.wanxin.finance.transaction.message;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;

import javax.annotation.Resource;

/**
 * 审核放款事务消息发送者
 *
 * @author luHan
 * @create 2021/6/23 11:21
 * @since 1.0.0
 */
@Component
@Slf4j
public class LoansApprovalStatusTransactionalProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void updateProjectStatusAndStartRepayment(Project project,
                                                     ProjectWithTendersDTO projectWithTendersDTO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("project", project);
        jsonObject.put("projectWithTendersDTO", projectWithTendersDTO);
        Message<String> message = MessageBuilder.withPayload(jsonObject.toJSONString()).build();
        // 发送事务消息,会先发送半消息到Broker,当Broker接收到COMMIT标记时会将消息投递到消费者,当接收到ROLLBACK时会保存消息,不会进行投递
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction("PID_START_REPAYMENT",
                "TP_START_REPAYMENT", message, null);
        log.info("事务消息发送结果:{}", transactionSendResult);
    }
}
