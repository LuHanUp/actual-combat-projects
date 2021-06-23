package top.luhancc.wanxin.finance.repayment.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.common.domain.model.repayment.RepaymentRequest;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentPlan;
import top.luhancc.wanxin.finance.repayment.service.RepaymentService;

/**
 * 接收确认还款事务消息，并实现调用本地事务和 进行事务回查
 *
 * @author luHan
 * @create 2021/6/23 15:32
 * @since 1.0.0
 */
@Component
@RocketMQTransactionListener(txProducerGroup = "PID_CONFIRM_REPAYMENT")
public class ConfirmRepaymentTransactionListener implements RocketMQLocalTransactionListener {
    @Autowired
    private RepaymentService repaymentService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        RepaymentPlan repaymentPlan = parseMessage2RepaymentPlan(msg);
        RepaymentRequest repaymentRequest = parseMessage2RepaymentRequest(msg);
        Boolean flag = repaymentService.confirmRepayment(repaymentPlan, repaymentRequest);
        return flag ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        RepaymentPlan repaymentPlan = parseMessage2RepaymentPlan(msg);
        // 通过是否将还款明细修改为已还款来确认是COMMIT还是ROLLBACK
        repaymentPlan = repaymentService.getByRepaymentPlanId(repaymentPlan.getId());
        boolean flag = repaymentPlan != null && "1".equalsIgnoreCase(repaymentPlan.getRepaymentStatus());
        return flag ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
    }

    private RepaymentPlan parseMessage2RepaymentPlan(Message msg) {
        //1.解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        return JSONObject.parseObject(jsonObject.getString("repaymentPlan"),
                RepaymentPlan.class);
    }

    private RepaymentRequest parseMessage2RepaymentRequest(Message msg) {
        //1.解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        return JSONObject
                .parseObject(jsonObject.getString("repaymentRequest"), RepaymentRequest.class);
    }
}
