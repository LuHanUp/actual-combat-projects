package top.luhancc.gulimall.ware.config;

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

/**
 * @author luHan
 * @create 2021/1/19 10:42
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false, null);
    }

    @Bean
    public Queue stockReleaseQueue() {
        return new Queue("stock.release.queue", true, false, false, null);
    }

    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");// 消息过期后发送到那个交换机
        arguments.put("x-dead-letter-routing-key", "stock.release");// 发送交换机的绑定key
        arguments.put("x-message-ttl", 1000 * 60 * 35);// 消息的过期时间
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange", "stock.release.#", null);
    }

    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange", "stock.locked", null);
    }
}
