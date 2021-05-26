package top.luhancc.gulimall.product.dao;

import org.apache.ibatis.annotations.Param;
import top.luhancc.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<String> getSkuSaleAttrValues(@Param("skuId") Long skuId);
}
