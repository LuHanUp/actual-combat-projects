package top.luhancc.gulimall.product.service.impl.getcatalogjson;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.gulimall.product.dao.CategoryDao;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.service.GetCateLogJsonService;
import top.luhancc.gulimall.product.vo.web.Category2Vo;
import top.luhancc.gulimall.product.vo.web.Category3Vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 从数据库中获取三级分类数据
 * <p>性能不太好</p>
 *
 * @author luHan
 * @create 2021/1/7 10:08
 * @since 1.0.0
 */
@Service("getCateLogJsonByDb")
public class GetCateLogJsonByDbImpl implements GetCateLogJsonService {
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Map<String, List<Category2Vo>> getCatalogJson() {
        // 1. 获取所有的一级分类
        List<CategoryEntity> levelOneCategories = getByParentId(0L);
        // 2. 封装数据
        Map<String, List<Category2Vo>> levelOneCategoryMap = levelOneCategories.stream().collect(Collectors.toMap(
                key -> key.getCatId().toString(),
                value -> {
                    // 查询每个一级分类的二级分类
                    List<CategoryEntity> levelTwoCategories = getByParentId(value.getCatId());
                    // 将CategoryEntity封装为Category2Vo
                    List<Category2Vo> category2VoList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(levelTwoCategories)) {
                        category2VoList = levelTwoCategories.stream().map(category2 -> {
                            // 获取2级分类的三级分类
                            List<CategoryEntity> levelThreeCategories = getByParentId(category2.getCatId());
                            List<Category3Vo> category3VoList = new ArrayList<>();
                            if (CollectionUtils.isNotEmpty(levelThreeCategories)) {
                                category3VoList = levelThreeCategories.stream().map(category3 -> {
                                    return Category3Vo.builder()
                                            .catalog2Id(category2.getCatId().toString())
                                            .id(category3.getCatId().toString())
                                            .name(category3.getName())
                                            .build();
                                }).collect(Collectors.toList());
                            }
                            return Category2Vo.builder()
                                    .catalog1Id(value.getCatId().toString())
                                    .catalog3List(category3VoList)
                                    .id(category2.getCatId().toString())
                                    .name(category2.getName())
                                    .build();
                        }).collect(Collectors.toList());
                    }
                    return category2VoList;
                }));
        return levelOneCategoryMap;
    }

    private List<CategoryEntity> getByParentId(Long parentId) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = Wrappers.lambdaQuery(CategoryEntity.class)
                .eq(CategoryEntity::getParentCid, parentId);
        return categoryDao.selectList(queryWrapper);
    }
}
