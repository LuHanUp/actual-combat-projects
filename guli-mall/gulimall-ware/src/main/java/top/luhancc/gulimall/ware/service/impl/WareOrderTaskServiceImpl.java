package top.luhancc.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.ware.dao.WareOrderTaskDao;
import top.luhancc.gulimall.ware.entity.WareOrderTaskEntity;
import top.luhancc.gulimall.ware.service.WareOrderTaskService;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

    @Override
    public PageUtils<WareOrderTaskEntity> queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils<>(page);
    }

    @Override
    public WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn) {
        LambdaQueryWrapper<WareOrderTaskEntity> queryWrapper = Wrappers.lambdaQuery(WareOrderTaskEntity.class)
                .eq(WareOrderTaskEntity::getOrderSn, orderSn);
        return this.getOne(queryWrapper);
    }

}