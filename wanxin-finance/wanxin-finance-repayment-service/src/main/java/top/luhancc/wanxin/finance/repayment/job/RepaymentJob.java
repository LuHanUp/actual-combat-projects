package top.luhancc.wanxin.finance.repayment.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.luhancc.wanxin.finance.repayment.service.RepaymentService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 定时执行还款任务
 *
 * @author luHan
 * @create 2021/6/24 11:16
 * @since 1.0.0
 */
@Component
public class RepaymentJob implements SimpleJob {
    @Autowired
    private RepaymentService repaymentService;

    @Override
    public void execute(ShardingContext shardingContext) {
        repaymentService.executeRepayment(LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                shardingContext.getShardingTotalCount(), shardingContext.getShardingItem());
    }
}
