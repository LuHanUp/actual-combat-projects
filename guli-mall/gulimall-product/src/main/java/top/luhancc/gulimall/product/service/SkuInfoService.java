package top.luhancc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.product.entity.SkuInfoEntity;
import top.luhancc.gulimall.product.vo.web.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils<SkuInfoEntity> queryPage(Map<String, Object> params);

    List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId);

    SkuItemVo itemDetail(Long skuId);
}

