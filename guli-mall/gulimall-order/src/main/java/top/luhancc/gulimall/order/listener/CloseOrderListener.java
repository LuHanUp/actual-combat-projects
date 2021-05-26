package top.luhancc.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.luhancc.gulimall.order.entity.OrderEntity;
import top.luhancc.gulimall.order.service.OrderService;

import java.io.IOException;

import static top.luhancc.gulimall.order.domain.order.vo.OrderConstant.ORDER_RELEASE_QUEUE;

/**
 * @author luHan
 * @create 2021/1/19 13:56
 * @since 1.0.0
 */
@Component
@RabbitListener(queues = ORDER_RELEASE_QUEUE)
@Slf4j
public class CloseOrderListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void closeOrderListener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        log.info("接收到过期订单信息,准备关闭订单[{}]", orderEntity.getOrderSn());
        try {
            orderService.closeOrder(orderEntity);
            /**
             * TODO 手动调用支付宝收单功能
             * {@link top.luhancc.gulimall.order.config.AlipayTemplate#close(String, String)}
             *
             * TODO 或者在凌晨调用支付宝查询订单进行对账来进行收单
             * {@link top.luhancc.gulimall.order.config.AlipayTemplate#query(String, String)}
             */
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("关闭订单失败,", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
