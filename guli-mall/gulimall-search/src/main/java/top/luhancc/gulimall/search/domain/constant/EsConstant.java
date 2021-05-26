package top.luhancc.gulimall.search.domain.constant;

/**
 * elasticSearch相关的常量类
 *
 * @author luHan
 * @create 2021/1/8 09:48
 * @since 1.0.0
 */
public final class EsConstant {

    /**
     * elasticSearch索引相关的常量
     */
    public static final class IndexConstant {
        /**
         * 商品数据的索引
         */
        public static final String PRODUCT_INDEX = "gulimal_product";
    }


    /**
     * 默认显示的页码
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认显示的条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 100;


    /**
     * ElasticSearch聚合分析的名称常量
     */
    public static final class AggNameConstant {
        public static final String BRAND_AGG = "brand_agg";
        public static final String BRAND_NAME_AGG = "brand_name_agg";
        public static final String BRAND_IMG_AGG = "brand_img_agg";

        public static final String CATALOG_AGG = "catalog_agg";
        public static final String CATALOG_NAME_AGG = "catalog_name_agg";

        public static final String ATTR_AGG = "attr_agg";
        public static final String ATTR_ID_AGG = "attr_id_agg";
        public static final String ATTR_NAME_AGG = "attr_name_agg";
        public static final String ATTR_VALUE_AGG = "attr_value_agg";
    }
}
