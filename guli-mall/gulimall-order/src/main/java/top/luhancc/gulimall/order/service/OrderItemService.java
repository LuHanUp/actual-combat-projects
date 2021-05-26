package top.luhancc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.order.entity.OrderItemEntity;

import java.util.List;
import java.util.Map;

/**
 * 订单项信息
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:50:19
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<OrderItemEntity> getByOrderSn(String orderSn);
}

