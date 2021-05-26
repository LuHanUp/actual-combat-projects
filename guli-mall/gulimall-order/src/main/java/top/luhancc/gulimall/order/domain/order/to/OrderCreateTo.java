package top.luhancc.gulimall.order.domain.order.to;

import lombok.Data;
import top.luhancc.gulimall.order.entity.OrderEntity;
import top.luhancc.gulimall.order.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author luHan
 * @create 2021/1/14 18:10
 * @since 1.0.0
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;// 订单信息
    private List<OrderItemEntity> items;// 订单项
    private BigDecimal payPrice;// 订单计算的应付价格
    private BigDecimal fare;// 运费
}
