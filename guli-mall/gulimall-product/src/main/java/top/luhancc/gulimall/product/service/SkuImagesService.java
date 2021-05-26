package top.luhancc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

