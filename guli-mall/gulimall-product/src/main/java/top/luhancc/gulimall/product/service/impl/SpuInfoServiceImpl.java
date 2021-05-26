package top.luhancc.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.common.to.search.ProductEsModule;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;
import top.luhancc.gulimall.product.dao.SpuInfoDao;
import top.luhancc.gulimall.product.entity.BrandEntity;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.entity.SkuInfoEntity;
import top.luhancc.gulimall.product.entity.SpuInfoEntity;
import top.luhancc.gulimall.product.service.BrandService;
import top.luhancc.gulimall.product.service.CategoryService;
import top.luhancc.gulimall.product.service.SkuInfoService;
import top.luhancc.gulimall.product.service.SpuInfoService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils<SpuInfoEntity> queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils<>(page);
    }

    @Override
    public void up(Long spuId) {
        // 1. 查询出的当前spuId对应的所有sku信息
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.getSkuInfoBySpuId(spuId);
        //TODO 2. 查询出当前sku所有的可检索的规格属性

        // 3. 封装成ProductEsModule数据
        List<ProductEsModule> upProducts = skuInfoEntityList.stream().map(skuInfoEntity -> {
            Long brandId = skuInfoEntity.getBrandId();
            BrandEntity brandEntity = brandService.getById(brandId);
            Long catalogId = skuInfoEntity.getCatalogId();
            CategoryEntity categoryEntity = categoryService.getById(catalogId);

            return ProductEsModule.builder()
                    .brandId(brandId)
                    .brandImg(brandEntity.getLogo())
                    .brandName(brandEntity.getName())
                    .catalogId(catalogId)
                    .catalogName(categoryEntity.getName())
                    //TODO 调用仓库服务的库存数量
                    .hasStock(true)
                    //TODO 设置热度评分
                    .hotScore(2L)
                    .saleCount(skuInfoEntity.getSaleCount())
                    .skuImg(skuInfoEntity.getSkuDefaultImg())
                    .skuPrice(skuInfoEntity.getPrice())
                    .skuTitle(skuInfoEntity.getSkuTitle())
                    .skuId(skuInfoEntity.getSkuId())
                    .spuId(skuInfoEntity.getSpuId())
//                    .attrs()
                    .build();
        }).collect(Collectors.toList());
        // 4. 数据发送给search服务进行保存
    }
}