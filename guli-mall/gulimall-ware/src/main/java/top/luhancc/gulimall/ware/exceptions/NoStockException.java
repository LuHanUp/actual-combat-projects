package top.luhancc.gulimall.ware.exceptions;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/18 14:10
 * @since 1.0.0
 */
@Data
public class NoStockException extends RuntimeException {
    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品[" + skuId + "]没有足够的库存了");
        this.skuId = skuId;
    }
}
