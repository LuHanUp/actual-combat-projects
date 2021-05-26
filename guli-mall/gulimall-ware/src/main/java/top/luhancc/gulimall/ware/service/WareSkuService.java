package top.luhancc.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.ware.domain.vo.LockStockResult;
import top.luhancc.gulimall.ware.domain.vo.WareSkuLockVo;
import top.luhancc.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:53:26
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 锁定订单库存
     *
     * @param wareSkuLockVo
     * @return
     */
    Boolean orderLockSku(WareSkuLockVo wareSkuLockVo);

    void unLockStock(Long skuId, Long wareId, Integer skuNum, Long detailId);
}

