package top.luhancc.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static top.luhancc.gulimall.order.domain.order.vo.OrderConstant.*;

/**
 * RabbitMQ配置
 * 如果给容器中放入了Queue、Binding、Exchange springboot会为我们自动创建(在第一次连接上RabbitMQ并且监听消息的时候才会自动创建,不存在的时候)
 * 如果RabbitMQ中已经存在了，即使参数改变了也不会进行覆盖
 *
 * @author luHan
 * @create 2021/1/18 17:51
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    /**
     * 延迟队列
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange", ORDER_EVENT_EXCHANGE);// 消息过期后发送到那个交换机
        arguments.put("x-dead-letter-routing-key", ORDER_RELEASE_QUEUE);// 发送交换机的绑定key
        arguments.put("x-message-ttl", ORDER_AUTO_CANCEL_TIME);// 消息的过期时间
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, arguments);
    }

    /**
     * 取消订单队列
     *
     * @return
     */
    @Bean
    public Queue orderReleaseQueue() {
        return new Queue(ORDER_RELEASE_QUEUE, true, false, false, null);
    }

    @Bean
    public Queue orderSeckillQueue() {
        return new Queue(ORDER_SECKILL_QUEUE, true, false, false, null);
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE, true, false, null);
    }

    @Bean
    public Binding orderCreateOrderBinding() {
        return new Binding(ORDER_DELAY_QUEUE, Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE,
                ORDER_CREATE_ORDER_TOPIC, null);
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding(ORDER_RELEASE_QUEUE, Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE,
                ORDER_RELEASE_ORDER_TOPIC, null);
    }

    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding("stock.release.queue", Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE,
                "order.release.other.#", null);
    }

    @Bean
    public Binding orderSeckillBinding() {
        return new Binding(ORDER_SECKILL_QUEUE, Binding.DestinationType.QUEUE, ORDER_EVENT_EXCHANGE,
                "order.seckill", null);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
