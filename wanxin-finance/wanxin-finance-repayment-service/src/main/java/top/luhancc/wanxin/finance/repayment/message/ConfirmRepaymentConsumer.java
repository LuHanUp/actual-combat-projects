package top.luhancc.wanxin.finance.repayment.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.common.domain.model.repayment.RepaymentRequest;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentPlan;
import top.luhancc.wanxin.finance.repayment.service.RepaymentService;

/**
 * 接收确认还款事务消息，调用确认还款接口
 *
 * @author luHan
 * @create 2021/6/23 16:16
 * @since 1.0.0
 */
@Component
@RocketMQMessageListener(topic = "TP_CONFIRM_REPAYMENT", consumerGroup = "CID_CONFIRM_REPAYMENT")
public class ConfirmRepaymentConsumer implements RocketMQListener<String> {
    @Autowired
    private RepaymentService repaymentService;

    @Override
    public void onMessage(String message) {
        // 1.解析消息
        JSONObject jsonObject = JSON.parseObject(message);
        RepaymentPlan repaymentPlan = JSONObject.parseObject(jsonObject.getString("repaymentPlan"), RepaymentPlan.class);
        RepaymentRequest repaymentRequest = JSONObject.parseObject(jsonObject.getString("repaymentRequest"),
                RepaymentRequest.class);
        // 2.执行业务
        repaymentService.invokeConfirmRepayment(repaymentPlan, repaymentRequest);
    }
}
