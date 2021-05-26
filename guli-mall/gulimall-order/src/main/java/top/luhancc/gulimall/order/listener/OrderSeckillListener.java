package top.luhancc.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.luhancc.common.to.coupon.SeckillOrderTo;
import top.luhancc.gulimall.order.entity.OrderEntity;
import top.luhancc.gulimall.order.service.OrderService;

import java.io.IOException;

import static top.luhancc.gulimall.order.domain.order.vo.OrderConstant.ORDER_SECKILL_QUEUE;

/**
 * @author luHan
 * @create 2021/1/21 11:17
 * @since 1.0.0
 */
@RabbitListener(queues = ORDER_SECKILL_QUEUE)
@Component
@Slf4j
public class OrderSeckillListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void closeOrderListener(SeckillOrderTo seckillOrderTo, Channel channel, Message message) throws IOException {
        log.info("接收到秒杀单信息:[{}]", seckillOrderTo);
        try {
            orderService.createSeckillOrder(seckillOrderTo);
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("关闭订单失败,", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
