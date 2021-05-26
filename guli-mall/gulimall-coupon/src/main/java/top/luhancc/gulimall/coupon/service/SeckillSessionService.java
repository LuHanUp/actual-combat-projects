package top.luhancc.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils<SeckillSessionEntity> queryPage(Map<String, Object> params);

    List<SeckillSessionEntity> getLast3DaySession();
}

