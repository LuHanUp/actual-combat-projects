package top.luhancc.gulimall.search.domain.vo.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import top.luhancc.common.to.search.ProductEsModule;

import java.util.List;

/**
 * 检索返回的数据模型
 *
 * @author luHan
 * @create 2021/1/7 18:06
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor
public class SearchResult {

    /**
     * 检索条件
     */
    private final SearchParam searchParam;

    /**
     * 面包屑导航数据
     */
    private List<NavVo> navs;

    /**
     * 查询到的所有商品信息
     */
    private List<ProductEsModule> products;

    /**
     * 查询到的结果涉及的所有品牌信息
     */
    private List<BrandVo> brands;

    /**
     * 查询到的结果涉及的所有属性信息
     */
    private List<AttrVo> attrs;

    /**
     * 查询到的结果涉及的所有分类信息
     */
    private List<CatalogVo> catalogs;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 数据总数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 品牌信息数据模型类
     */
    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    /**
     * 分类信息数据模型类
     */
    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    /**
     * 属性信息数据模型类
     */
    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    /**
     * 面包屑导航数据模型类
     */
    @Data
    public static class NavVo {
        /**
         * 导航的名称
         */
        private String navName;

        /**
         * 导航的值
         */
        private Object navValue;

        /**
         * 跳转的地址
         */
        private String link;
    }
}
