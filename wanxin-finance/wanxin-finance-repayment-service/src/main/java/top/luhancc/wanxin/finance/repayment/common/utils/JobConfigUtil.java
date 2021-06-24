package top.luhancc.wanxin.finance.repayment.common.utils;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;

/**
 * @author luHan
 * @create 2021/6/24 11:21
 * @since 1.0.0
 */
public final class JobConfigUtil {
    /**
     * 配置任务详细信息
     *
     * @param jobClass           任务执行类
     * @param cron               执行策略
     * @param shardingTotalCount 分片数量
     * @return
     */
    public static LiteJobConfiguration createJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                              final String cron,
                                                              final int shardingTotalCount) {
        //创建JobCoreConfigurationBuilder
        JobCoreConfiguration.Builder jobCoreConfigurationBuilder = JobCoreConfiguration.newBuilder(
                jobClass.getName(), cron, shardingTotalCount);
        JobCoreConfiguration jobCoreConfiguration = jobCoreConfigurationBuilder.build();
        //创建SimpleJobConfiguration
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration,
                jobClass.getCanonicalName());
        //创建LiteJobConfiguration
        return LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
    }
}
