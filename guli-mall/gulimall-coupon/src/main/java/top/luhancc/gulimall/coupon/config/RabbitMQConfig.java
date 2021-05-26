package top.luhancc.gulimall.coupon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
