package top.luhancc.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.common.utils.R;
import top.luhancc.gulimall.product.dao.SkuInfoDao;
import top.luhancc.gulimall.product.entity.AttrGroupEntity;
import top.luhancc.gulimall.product.entity.SkuImagesEntity;
import top.luhancc.gulimall.product.entity.SkuInfoEntity;
import top.luhancc.gulimall.product.entity.SpuInfoDescEntity;
import top.luhancc.gulimall.product.feign.SeckillFeign;
import top.luhancc.gulimall.product.service.*;
import top.luhancc.gulimall.product.vo.web.SeckillInfoVo;
import top.luhancc.gulimall.product.vo.web.SkuItemVo;


/**
 * @author luhan
 */
@Service("skuInfoService")
@RequiredArgsConstructor
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    private final SkuImagesService skuImagesService;
    private final SpuInfoDescService spuInfoDescService;
    private final AttrGroupService attrGroupService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final ThreadPoolExecutor executor;
    private final SeckillFeign seckillFeign;

    @Override
    public PageUtils<SkuInfoEntity> queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );
        return new PageUtils<>(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = Wrappers.lambdaQuery(SkuInfoEntity.class)
                .eq(SkuInfoEntity::getSpuId, spuId);
        return this.list(queryWrapper);
    }

    @Override
    public SkuItemVo itemDetail(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1. sku基本信息的获取
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setSkuInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);
        // 2. sku的图片信息获取
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);
        // 3. 获取spu销售属性组合
        CompletableFuture<Void> saleAttrFuture = CompletableFuture.runAsync(() -> {
            List<SkuItemVo.ItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(skuId);
            skuItemVo.setSaleAttrs(saleAttrVos);
        }, executor);
        // 4. spu的介绍
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            Long spuId = res.getSpuId();
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(spuId);
            skuItemVo.setSpuInfoDesc(spuInfoDesc);
        }, executor);
        // 5. 获取spu的规格参数信息
        CompletableFuture<Void> groupAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SkuItemVo.SpuItemGroupAttrVo> groupAttrVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(groupAttrVos);
        }, executor);
        // 6. 获取商品参与的秒杀数据
        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            R r = seckillFeign.getSkuSeckillInfo(skuId);
            if (r.isSuccess()) {
                SeckillInfoVo seckillInfoVo = r.get(SeckillInfoVo.class);
                skuItemVo.setSeckillInfo(seckillInfoVo);
            }
        }, executor);
        // 等待所有任务都执行完,必须要使用join方法,不然任务不会执行
        CompletableFuture.allOf(infoFuture, imagesFuture, saleAttrFuture, descFuture, groupAttrFuture, seckillFuture).join();
        return skuItemVo;
    }
}