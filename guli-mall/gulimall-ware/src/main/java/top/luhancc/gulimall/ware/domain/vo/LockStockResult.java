package top.luhancc.gulimall.ware.domain.vo;

import lombok.Data;

/**
 * 每个订单项锁定库存的结果类
 *
 * @author luHan
 * @create 2021/1/18 13:57
 * @since 1.0.0
 */
@Data
public class LockStockResult {
    private Long skuId;// 商品id
    private Integer num;// 锁定的件数
    private Boolean lock;// 是否成功锁定 true-是 false-否
}
