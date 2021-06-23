package top.luhancc.wanxin.finance.transaction.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.transaction.common.constant.ProjectCode;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;
import top.luhancc.wanxin.finance.transaction.service.ProjectService;

/**
 * 事务消息的监听器,确定是COMMIT还是ROLLBACK
 * <p>
 * RocketMQ解决放款中事务的一致性流程图.jpg中的第3. 4.步
 *
 * @author luHan
 * @create 2021/6/23 11:41
 * @since 1.0.0
 */
@Component
@RocketMQTransactionListener(txProducerGroup = "PID_START_REPAYMENT")
public class LoansApprovalStatusTransactionalListener implements RocketMQLocalTransactionListener {
    @Autowired
    private ProjectService projectService;

    /**
     * 执行本地事务方法(执行修改标的状态为还款中)，返回COMMIT还是ROLLBACK
     *
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        Project project = parseMessage2Project(msg);
        // 更新标的状态为还款中
        Boolean flag = projectService.updateProjectStatusAndStartRepayment(project);
        return flag ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
    }

    /**
     * 事务回查，确认本地事务是否执行成功
     *
     * @param msg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        Project project = parseMessage2Project(msg);
        // 查询标的状态是否为还款中,如果不是就返回ROLLBACK
        project = projectService.getById(project.getId());
        if (ProjectCode.REPAYING.getCode().equalsIgnoreCase(project.getProjectStatus())) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    /**
     * 从消息中提取Project对象
     *
     * @param msg
     * @return
     */
    private Project parseMessage2Project(Message msg) {
        JSONObject jsonObject = JSON.parseObject(new String((byte[]) msg.getPayload()));
        return JSON.parseObject(jsonObject.getString("project"), Project.class);
    }
}
