package top.luhancc.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.coupon.dao.SeckillSessionDao;
import top.luhancc.gulimall.coupon.entity.SeckillSessionEntity;
import top.luhancc.gulimall.coupon.entity.SeckillSkuRelationEntity;
import top.luhancc.gulimall.coupon.service.SeckillSessionService;
import top.luhancc.gulimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils<SeckillSessionEntity> queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils<>(page);
    }

    @Override
    public List<SeckillSessionEntity> getLast3DaySession() {
        // 计算最近三天
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(3);
        /**
         * SELECT * FROM sms_seckill_session WHERE start_time BETWEEN '' AND ''
         */
        LambdaQueryWrapper<SeckillSessionEntity> queryWrapper = Wrappers.lambdaQuery(SeckillSessionEntity.class)
                .between(SeckillSessionEntity::getStartTime,
                        LocalDateTime.of(now, LocalTime.MIN),
                        LocalDateTime.of(endDate, LocalTime.MAX));
        List<SeckillSessionEntity> seckillSessionEntities = list(queryWrapper);
        for (SeckillSessionEntity seckillSessionEntity : seckillSessionEntities) {
            List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.getBySessionId(seckillSessionEntity.getId());
            seckillSessionEntity.setRelationSkuList(relationEntities);
        }
        return seckillSessionEntities;
    }

}