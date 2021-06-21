package top.luhancc.wanxin.finance.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Tender;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于操作投标信息的Mapper
 *
 * @author luHan
 * @create 2021/6/21 14:19
 * @since 1.0.0
 */
public interface TenderMapper extends BaseMapper<Tender> {

    /**
     * 根据标的id, 获取标的已投金额, 如果未投返回0.0
     *
     * @param id
     * @return 为什么是个集合？因为进行了分库分表，所以得的结果可能是多条记录
     */
    List<BigDecimal> selectAmountInvestedByProjectId(@Param("id") Long id);
}
