package top.luhancc.gulimall.product.vo.web;

import lombok.Data;
import top.luhancc.gulimall.product.entity.SkuImagesEntity;
import top.luhancc.gulimall.product.entity.SkuInfoEntity;
import top.luhancc.gulimall.product.entity.SpuInfoDescEntity;

import java.util.List;

/**
 * 商品详情vo
 *
 * @author luHan
 * @create 2021/1/11 10:02
 * @since 1.0.0
 */
@Data
public class SkuItemVo {
    /**
     * sku基本信息
     */
    private SkuInfoEntity skuInfo;

    /**
     * sku的图片信息
     */
    private List<SkuImagesEntity> images;

    /**
     * spu销售属性组合
     */
    private List<ItemSaleAttrVo> saleAttrs;

    /**
     * spu的介绍
     */
    private SpuInfoDescEntity spuInfoDesc;

    /**
     * spu的规格参数信息
     */
    private List<SpuItemGroupAttrVo> groupAttrs;

    private SeckillInfoVo seckillInfo;


    /**
     * 销售属性的vo
     */
    @Data
    public static class ItemSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }

    /**
     * spu的规则参数信息
     */
    @Data
    public static class SpuItemGroupAttrVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    /**
     * spu的基本属性信息
     */
    @Data
    public static class SpuBaseAttrVo {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
