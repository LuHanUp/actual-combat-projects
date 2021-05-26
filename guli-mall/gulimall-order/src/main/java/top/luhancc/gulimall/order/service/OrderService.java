package top.luhancc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.to.coupon.SeckillOrderTo;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.order.domain.order.vo.*;
import top.luhancc.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:50:20
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirm();

    SubmitOrderResultVo submitOrder(OrderSubmitVo orderSubmitVo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    String handlePayResult(PayAsyncVo payAsyncVo);

    void createSeckillOrder(SeckillOrderTo seckillOrderTo);
}

