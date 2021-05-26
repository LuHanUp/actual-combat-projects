package top.luhancc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.product.entity.SkuSaleAttrValueEntity;
import top.luhancc.gulimall.product.vo.web.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemVo.ItemSaleAttrVo> getSaleAttrsBySpuId(Long skuId);

    List<String> getSkuSaleAttrValues(Long skuId);
}

