package top.luhancc.gulimall.order.domain.order.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页需要用到的数据模型类
 *
 * @author luHan
 * @create 2021/1/14 14:33
 * @since 1.0.0
 */
@Data
public class OrderConfirmVo {
    /**
     * 收货地址
     */
    private List<MemberAddressVo> address;

    /**
     * 订单项
     */
    private List<OrderItemVo> orderItemVos;

    // 发票信息。。。

    /**
     * 优惠信息  只演示积分
     */
    private Integer integration;

    private String orderToken;

    /**
     * 订单总额
     */
//    private BigDecimal totalPrice;
    public BigDecimal getTotalPrice() {
        BigDecimal sum = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(orderItemVos)) {
            for (OrderItemVo orderItemVo : orderItemVos) {
                BigDecimal price = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount()));
                sum = sum.add(price);
            }
        }
        return sum;
    }

    /**
     * 应付金额
     */
//    private BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }
}
