package top.luhancc.gulimall.search.domain.vo.web;

import lombok.Data;
import top.luhancc.gulimall.search.domain.constant.EsConstant;

import java.util.List;

/**
 * 检索条件类
 * <p>
 * 1. 全文检索：skuTitle ===》 keyword
 * 2. 排序：saleCount销量 hotScore热度 skuPrice价格 ===》sort
 * 3. 过滤：hasStock是否有货 skuPrice区间 brandId品牌id catalog3Id三级分类id attrs
 * 4. 聚合：attrs
 *
 * @author luHan
 * @create 2021/1/7 17:29
 * @since 1.0.0
 */
@Data
public class SearchParam {

    /**
     * 全文匹配的关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * sort=skuPrice_asc/desc
     */
    private String sort;

    /**
     * 是否只显示有货 0/1
     * 0:无库存
     * 1:有库存
     */
    private Integer hasStock;

    /**
     * 价格区间
     * skuPrice=100_500 价格在100到500之间
     * skuPrice=_500 价格在小于等于500
     * skuPrice=500_ 价格在大于等于500
     */
    private String skuPrice;

    /**
     * 品牌id
     * 多个brandId=1&brandId=2
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选
     * attrs=属性id_属性值:属性值...
     */
    private List<String> attrs;

    /**
     * 当前第几页
     */
    private Integer pageNum = EsConstant.DEFAULT_PAGE_NUM;

    /**
     * 每页显示条数
     */
    private Integer size = EsConstant.DEFAULT_PAGE_SIZE;


    //================================元数据,前端不用管=======================================

    /**
     * 请求参数字符串形式
     */
    private String _queryString;
}
