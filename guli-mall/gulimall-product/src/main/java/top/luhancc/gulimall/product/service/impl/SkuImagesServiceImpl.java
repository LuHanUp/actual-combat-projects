package top.luhancc.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.product.dao.SkuImagesDao;
import top.luhancc.gulimall.product.entity.SkuImagesEntity;
import top.luhancc.gulimall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuImagesEntity> queryWrapper = Wrappers.lambdaQuery(SkuImagesEntity.class)
                .eq(SkuImagesEntity::getSkuId, skuId);
        return list(queryWrapper);
    }

}