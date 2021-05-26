package top.luhancc.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author luHan
 * @create 2021/1/14 10:04
 * @since 1.0.0
 */
@Component
@Slf4j
public class RabbitConsumer {

    /**
     * 接收RabbitMQ中队列的消息
     * queues：需要监听的队列
     * <p>
     * 注解@RabbitListener可以标注在类+方法上
     * 注解@RabbitHandler只可以标注在方法上
     * 当一个类需要接受不同类型的消息时就可以使用@RabbitListener+@RabbitHandler组合的方式
     * <pre>
     * {@code
     *     @RabbitLisener(queue="xxxx")
     *     public class RabbitConsumer{
     *          @RabbitHandler
     *          public void readMessage(Order order){}
     *
     *          @RabbitHandler
     *          public void readMessage(Coupon coupon){}
     *     }
     * }
     * </pre>
     *
     * @param message 参数可以写如下类型：
     *                1、{@link org.springframework.amqp.core.Message} message 原生消息详细信息
     *                2. T 当时发送的什么类型就可以写什么类型(如果需要直接转化为实体类,需要设置序列化方式)
     *                3. ${@link com.rabbitmq.client.Channel} channel 当前传输数据通道
     */
    @RabbitListener(queues = "test.java.queue")
    public void testReadMessage(Message message, String msg, Channel channel) {
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        log.info("接收到的消息:{}", message);
    }
}
