package top.luhancc.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.order.dao.OrderItemDao;
import top.luhancc.gulimall.order.entity.OrderItemEntity;
import top.luhancc.gulimall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils<OrderItemEntity> queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils<>(page);
    }

    @Override
    public List<OrderItemEntity> getByOrderSn(String orderSn) {
        LambdaQueryWrapper<OrderItemEntity> queryWrapper = Wrappers.lambdaQuery(OrderItemEntity.class)
                .eq(OrderItemEntity::getOrderSn, orderSn);
        return this.list(queryWrapper);
    }
}