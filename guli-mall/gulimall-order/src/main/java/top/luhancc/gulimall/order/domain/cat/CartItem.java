package top.luhancc.gulimall.order.domain.cat;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 购物车中的购物项数据模型类
 *
 * @author luHan
 * @create 2021/1/13 10:18
 * @since 1.0.0
 */
@Data
public class CartItem {
    private Long skuId;
    private Boolean check;
    private String title;
    private String image;
    private List<String> attr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;


    public BigDecimal getTotalPrice() {
        if (count == null) {
            count = 0;
        }
        return price.multiply(new BigDecimal(count));
    }
}
