package top.luhancc.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.ObjectUtils;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.coupon.dao.SeckillSkuRelationDao;
import top.luhancc.gulimall.coupon.entity.SeckillSkuRelationEntity;
import top.luhancc.gulimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils<SeckillSkuRelationEntity> queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<>();
        Integer promotionSessionId = (Integer) params.get("promotionSessionId");
        queryWrapper.eq(promotionSessionId != null, "promotion_session_id", promotionSessionId);
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils<>(page);
    }

    @Override
    public List<SeckillSkuRelationEntity> getBySessionId(Long sessionId) {
        LambdaQueryWrapper<SeckillSkuRelationEntity> queryWrapper = Wrappers.lambdaQuery(SeckillSkuRelationEntity.class)
                .eq(SeckillSkuRelationEntity::getPromotionSessionId, sessionId);
        return this.list(queryWrapper);
    }
}