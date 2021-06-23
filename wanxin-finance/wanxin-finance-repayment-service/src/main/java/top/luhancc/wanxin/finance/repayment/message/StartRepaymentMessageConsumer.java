package top.luhancc.wanxin.finance.repayment.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryReturnCode;
import top.luhancc.wanxin.finance.common.domain.model.repayment.ProjectWithTendersDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.repayment.service.RepaymentService;

/**
 * 接收生成还款计划的消息，来生成还款计划
 * <p>
 * RocketMQ解决放款中事务的一致性流程图.jpg中的第8.步
 *
 * @author luHan
 * @create 2021/6/23 13:23
 * @since 1.0.0
 */
@Component
@RocketMQMessageListener(consumerGroup = "CID_START_REPAYMENT", topic = "TP_START_REPAYMENT")
public class StartRepaymentMessageConsumer implements RocketMQListener<String> {
    @Autowired
    private RepaymentService repaymentService;

    @Override
    public void onMessage(String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        ProjectWithTendersDTO projectWithTendersDTO = JSON.parseObject(jsonObject.getString("projectWithTendersDTO"), ProjectWithTendersDTO.class);
        ProjectDTO projectDTO = projectWithTendersDTO.getProject();
        // 先看这个标的是否生成过还款计划,如果没有生成过再进行生成 ==== 幂等
        int count = repaymentService.getRepaymentCountByProjectIdAndConsumerId(projectDTO.getConsumerId(), projectDTO.getId());
        if (count <= 0) {
            String returnCode = repaymentService.startRepayment(projectWithTendersDTO);
            if (!DepositoryReturnCode.RETURN_CODE_00000.getCode().equalsIgnoreCase(returnCode)) {
                throw new BusinessException(returnCode);
            }
        }
    }
}
