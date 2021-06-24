package top.luhancc.wanxin.finance.repayment.job;

import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.luhancc.wanxin.finance.repayment.common.utils.JobConfigUtil;

/**
 * 定时还款任务配置类
 *
 * @author luHan
 * @create 2021/6/24 11:16
 * @since 1.0.0
 */
@Configuration
public class RepaymentJobConfig {
    @Autowired
    private RepaymentJob repaymentJob;
    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Value("${job.repayment-job.count}")
    private int shardingCount;
    @Value("${job.repayment-job.cron}")
    private String cron;

    @Bean(initMethod = "init")
    public SpringJobScheduler initSimpleElasticJob() {
        //创建SpringJobScheduler
        return new SpringJobScheduler(repaymentJob, registryCenter,
                JobConfigUtil.createJobConfiguration(repaymentJob.getClass(), cron, shardingCount));
    }
}
