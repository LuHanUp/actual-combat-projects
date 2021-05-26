package top.luhancc.gulimall.ware.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author luHan
 * @create 2021/1/18 13:54
 * @since 1.0.0
 */
@Data
public class WareSkuLockVo {
    private String orderSn;// 订单号
    private List<OrderItemVo> locks;// 需要锁住的所有库存信息
}
