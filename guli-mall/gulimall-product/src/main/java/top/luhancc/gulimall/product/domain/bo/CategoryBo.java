package top.luhancc.gulimall.product.domain.bo;

import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 商品三级分类 BO
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
@Data
public class CategoryBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
    private Long catId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父分类id
     */
    private Long parentCid;
    /**
     * 层级
     */
    private Integer catLevel;
    /**
     * 是否显示[0-不显示，1显示]
     */
    private Integer showStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图标地址
     */
    private String icon;
    /**
     * 计量单位
     */
    private String productUnit;
    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 子分类
     */
    private List<CategoryBo> children;

}
