package top.luhancc.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.product.dao.SkuSaleAttrValueDao;
import top.luhancc.gulimall.product.entity.SkuSaleAttrValueEntity;
import top.luhancc.gulimall.product.service.SkuSaleAttrValueService;
import top.luhancc.gulimall.product.vo.web.SkuItemVo;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.ItemSaleAttrVo> getSaleAttrsBySpuId(Long skuId) {
        /*
            SELECT ssav.attr_id, ssav.attr_name, group_concat(DISTINCT ssav.attr_value)
            FROM pms_sku_info info
                LEFT JOIN pms_sku_sale_attr_value ssav on ssav.sku_id = info.sku_id
            WHERE info.spu_id = 13
            GROUP BY ssav.attr_id, ssav.attr_name
         */
        return null;
    }

    @Override
    public List<String> getSkuSaleAttrValues(Long skuId) {
        /*
            SELECT CONCAT(attr_name, ':', attr_value)
            FROM pms_sku_sale_attr_value
            where sku_id = 1;
         */
        return baseMapper.getSkuSaleAttrValues(skuId);
    }
}