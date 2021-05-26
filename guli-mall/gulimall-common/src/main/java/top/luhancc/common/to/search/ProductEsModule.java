package top.luhancc.common.to.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 存储在ES中的数据模型类
 *
 * @author luHan
 * @create 2021/1/5 11:24
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEsModule {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    /**
     * 热度评分
     */
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attr> attrs;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}