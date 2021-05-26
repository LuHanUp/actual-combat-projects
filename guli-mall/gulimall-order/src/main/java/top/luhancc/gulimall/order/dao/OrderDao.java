package top.luhancc.gulimall.order.dao;

import org.apache.ibatis.annotations.Param;
import top.luhancc.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:50:20
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 修改订单状态
     *
     * @param orderSn 订单号
     * @param status  状态 {@link top.luhancc.gulimall.order.domain.order.enume.OrderStatusEnum}
     */
    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("status") Integer status);
}
