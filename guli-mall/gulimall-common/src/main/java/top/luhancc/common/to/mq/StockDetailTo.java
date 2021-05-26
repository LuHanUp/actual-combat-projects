package top.luhancc.common.to.mq;

import lombok.Data;

/**
 * @author luHan
 * @create 2021/1/19 11:16
 * @since 1.0.0
 */
@Data
public class StockDetailTo {
    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;
}
