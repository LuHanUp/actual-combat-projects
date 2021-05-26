package top.luhancc.gulimall.ware.dao;

import org.apache.ibatis.annotations.Param;
import top.luhancc.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品库存
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:53:26
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    int lockSkuStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("lockNum") Integer lockNum);

    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("lockNum") Integer lockNum);
}
