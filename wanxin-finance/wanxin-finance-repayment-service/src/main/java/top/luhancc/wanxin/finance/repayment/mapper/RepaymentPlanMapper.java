package top.luhancc.wanxin.finance.repayment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.luhancc.wanxin.finance.repayment.mapper.entity.RepaymentPlan;

import java.util.List;

/**
 * 借款人还款计划 Mapper 接口
 *
 * @author luHan
 * @create 2021/6/22 17:08
 * @since 1.0.0
 */
public interface RepaymentPlanMapper extends BaseMapper<RepaymentPlan> {

    /**
     * 查询所有到期的还款计划
     *
     * @param date 到期时间
     * @return
     */
    List<RepaymentPlan> selectDueRepayment(@Param("date") String date);

    /**
     * 分片查询即将到期的还款计划
     *
     * @param date          到期时间
     * @param shardingTotal 分片总数
     * @param shardingItem  当前分片值
     * @return
     */
    List<RepaymentPlan> selectDueRepaymentSharding(@Param("date") String date,
                                                   @Param("shardingTotal") int shardingTotal,
                                                   @Param("shardingItem") int shardingItem);
}
