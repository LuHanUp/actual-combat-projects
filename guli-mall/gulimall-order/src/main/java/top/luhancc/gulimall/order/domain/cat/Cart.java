package top.luhancc.gulimall.order.domain.cat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;


/**
 * 购物车数据模型类
 *
 * @author luHan
 * @create 2021/1/13 10:18
 * @since 1.0.0
 */
@ToString
public class Cart {

    /**
     * 购物项
     */
    @Getter
    @Setter
    private List<CartItem> items;

    /**
     * 商品数量
     */
    private Integer countNum;

    /**
     * 商品类型数量
     */
    @Getter
    @Setter
    private Integer countType;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    /**
     * 减免价格
     */
    @Getter
    @Setter
    private BigDecimal reduce = new BigDecimal(0);

    public Integer getCountNum() {
        int countNum = 0;
        if (!CollectionUtils.isEmpty(items)) {
            countNum = items.stream().mapToInt(CartItem::getCount).sum();
        }
        this.countNum = countNum;
        return this.countNum;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItem item : items) {
                if (item.getCheck()) {
                    totalAmount = totalAmount.add(item.getTotalPrice());
                }
            }
        }
        // 减去减免价格
        totalAmount = totalAmount.subtract(reduce);
        this.totalAmount = totalAmount;
        return totalAmount;
    }

//    public Integer getCountType() {
//        return countType;
//    }
}
