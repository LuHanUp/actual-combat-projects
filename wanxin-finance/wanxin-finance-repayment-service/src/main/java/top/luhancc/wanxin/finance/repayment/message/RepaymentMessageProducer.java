package top.luhancc.wanxin.finance.repayment.message;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.common.domain.model.repayment.RepaymentRequest;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentPlan;

import javax.annotation.Resource;

/**
 * 发送确认还款事务半消息
 *
 * @author luHan
 * @create 2021/6/23 15:09
 * @since 1.0.0
 */
@Component
public class RepaymentMessageProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void confirmRepayment(RepaymentPlan repaymentPlan, RepaymentRequest repaymentRequest) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repaymentPlan", repaymentPlan);
        jsonObject.put("repaymentRequest", repaymentRequest);
        Message<String> message = MessageBuilder.withPayload(jsonObject.toJSONString()).build();
        rocketMQTemplate.sendMessageInTransaction("PID_CONFIRM_REPAYMENT",
                "TP_CONFIRM_REPAYMENT", message, null);
    }
}
