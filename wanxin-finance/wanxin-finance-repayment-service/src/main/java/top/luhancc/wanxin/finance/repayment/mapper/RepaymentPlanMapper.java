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
    List<RepaymentPlan> selectDueRepayment(@Param("date") String date);
}
