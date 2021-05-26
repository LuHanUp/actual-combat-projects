package top.luhancc.gulimall.order.domain.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单提交的数据
 *
 * @author luHan
 * @create 2021/1/14 17:38
 * @since 1.0.0
 */
@Data
public class OrderSubmitVo {
    private Long addrId;// 收货地址id
    private Integer payType;// 支付方式
    //    private List<String> items;// 不需要提交,去购物车获取
    private String token;// 令牌
    private BigDecimal payPrice;// 应付价格,可以用来做验价,当提交的这个价格和购物车获取过来的价格不一致时可以提示用户确认一个价格的变化
//    private String note;// 订单备注
}
