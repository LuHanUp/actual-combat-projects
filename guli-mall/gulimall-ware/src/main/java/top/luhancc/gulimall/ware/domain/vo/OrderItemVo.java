package top.luhancc.gulimall.ware.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单项
 *
 * @author luHan
 * @create 2021/1/14 14:38
 * @since 1.0.0
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> attr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
}
