package top.luhancc.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SeckillSkuRelationEntity> getBySessionId(Long sessionId);
}

