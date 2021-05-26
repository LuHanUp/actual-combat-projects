package top.luhancc.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.PojoUtils;
import top.luhancc.common.utils.Query;
import top.luhancc.gulimall.product.dao.CategoryDao;
import top.luhancc.gulimall.product.domain.bo.CategoryBo;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.service.CategoryService;
import top.luhancc.gulimall.product.service.GetCateLogJsonService;
import top.luhancc.gulimall.product.vo.web.Category2Vo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
//    @Qualifier("getCateLogJsonByDb") // 每级分类都从数据库中获取
//    @Qualifier("getCateLogJsonByMemory") // 先获取所有的分类，然后在程序中筛选一级、二级、三级
//    @Qualifier("getCateLogJsonByCache") // 先从缓存中获取，没有再去数据库获取
    @Qualifier("getCateLogJsonByCacheAndRedisLock") // 加上缓存的同时+分布式锁
    private GetCateLogJsonService cateLogJsonService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<CategoryBo> listWithTree() {
        // 查询出所有的分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        List<CategoryBo> categoryBos = PojoUtils.convertList(entities, CategoryBo.class);
        // 组装成父子结构
        // 1. 找出所有的一级分类
        List<CategoryBo> level1Category = categoryBos.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(categoryBo -> {
                    // 2. 找出所有的子分类
                    categoryBo.setChildren(getChildren(categoryBo, categoryBos));
                    return categoryBo;
                })
                .sorted((c1, c2) -> {
                    int c1Sort = c1.getSort() == null ? 0 : c1.getSort();
                    int c2Sort = c2.getSort() == null ? 0 : c2.getSort();
                    return c1Sort - c2Sort;
                })
                .collect(Collectors.toList());
        return level1Category;
    }

    @Override
    public void removeCategoryByIds(List<Long> catIds) {
        //TODO 1. 检查当前删除的分类，是否被别的地方所引用
        //TODO 2. 进行逻辑删除
        this.removeByIds(catIds);
    }

    @Override
    public List<CategoryEntity> getLevelOneCategories() {
        return getByParentId(0L);
    }

    @Override
    public Map<String, List<Category2Vo>> getCatalogJson() {
        return cateLogJsonService.getCatalogJson();
    }

    /**
     * 得到当前分类的所有子分类
     *
     * @param category    当前分类
     * @param categoryBos 所有的分类信息
     * @return 当前分类的子分类
     */
    private List<CategoryBo> getChildren(CategoryBo category, List<CategoryBo> categoryBos) {
        List<CategoryBo> childrenCategories = categoryBos.stream()
                .filter(categoryBo -> categoryBo.getParentCid().equals(category.getCatId()))
                .map(categoryBo -> {
                    // 递归找出当前分类的所有子分类
                    categoryBo.setChildren(getChildren(categoryBo, categoryBos));
                    return categoryBo;
                })
                .sorted((c1, c2) -> {
                    int c1Sort = c1.getSort() == null ? 0 : c1.getSort();
                    int c2Sort = c2.getSort() == null ? 0 : c2.getSort();
                    return c1Sort - c2Sort;
                })
                .collect(Collectors.toList());
        return childrenCategories;
    }

    private List<CategoryEntity> getByParentId(Long parentId) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = Wrappers.lambdaQuery(CategoryEntity.class)
                .eq(CategoryEntity::getParentCid, parentId);
        return this.list(queryWrapper);
    }
}