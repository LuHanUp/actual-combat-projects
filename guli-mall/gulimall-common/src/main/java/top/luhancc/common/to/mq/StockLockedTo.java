package top.luhancc.common.to.mq;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/19 11:10
 * @since 1.0.0
 */
@Data
public class StockLockedTo {
    private Long id;// 库存工作单的id
    private StockDetailTo stockDetail;// 工作单详情的id
}
