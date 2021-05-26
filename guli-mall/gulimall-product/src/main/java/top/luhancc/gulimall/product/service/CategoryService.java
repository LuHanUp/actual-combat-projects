package top.luhancc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.product.domain.bo.CategoryBo;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.vo.web.Category2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询分类数据并组装成树形结构
     *
     * @return
     */
    List<CategoryBo> listWithTree();

    /**
     * 批量删除分类数据
     *
     * @param catIds 分类的id
     */
    void removeCategoryByIds(List<Long> catIds);

    /**
     * 查询出所有的1级分类
     *
     * @return
     */
    List<CategoryEntity> getLevelOneCategories();

    Map<String, List<Category2Vo>> getCatalogJson();
}

