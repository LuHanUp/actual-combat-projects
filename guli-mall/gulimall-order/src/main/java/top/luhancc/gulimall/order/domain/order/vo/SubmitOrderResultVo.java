package top.luhancc.gulimall.order.domain.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.luhancc.gulimall.order.entity.OrderEntity;

/**
 * @author luHan
 * @create 2021/1/14 17:57
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitOrderResultVo {
    private OrderEntity order;// 订单信息
    private Integer code;// 订单状态码
}
