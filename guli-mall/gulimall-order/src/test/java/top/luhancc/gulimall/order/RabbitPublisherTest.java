package top.luhancc.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Rabbit发送者测试
 * <p>
 * 接收者请参考:{@link top.luhancc.gulimall.order.service.impl.RabbitConsumer}
 * <p>
 * 消息确认机制:可靠抵达,需要生产者和接收者配合工作
 * 发送者确认 confirmCallback确认模式+publisher：returnCallback 未投递到queue退回模式
 * confirmCallback确认模式
 * 1. 设置publisher-confirms=true(新版使用publisher-confirm-type=correlated代替)
 * 2. rabbitTemplate.setConfirmCallback
 * returnCallback 未投递到queue退回模式
 * 1. 设置publisher-returns: true
 * 2. 设置template.mandatory: true # 只要消息抵达队列，以异步发送优先回调returnCallback
 * 3. rabbitTemplate.setReturnCallback
 * 接收者确认 consumer：ack机制
 * ack机制
 * 1. 开启手动ack机制listener.simple.acknowledge-mode: manual
 * 2. 接收消息的方法需要接收一个Channel参数,调用其方法来确认消息被消费了
 * {@code channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);}
 *
 * @author luHan
 * @create 2021/1/14 10:27
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RabbitPublisherTest {
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCreateExchange() {
        // String name:交换机的名称
        // boolean durable:是否进行持久化 true-是 false-否
        // boolean autoDelete:是否自动删除 true-是 false-否
        // Map<String, Object> arguments
        Exchange exchange = new DirectExchange("test.java.exchange", true, false, null);
        amqpAdmin.declareExchange(exchange);
        log.info("exchange[{}]创建成功", exchange.getName());
    }

    @Test
    public void testCreateQueue() {
        // String name:队列的名称
        // boolean durable:队列是否持久化 true-是 false-否
        // boolean exclusive:是否是排他的队列，只有当创建这个队列的连接才能使用这个队列 true-是 false-否
        // boolean autoDelete:是否自动删除 true-是 false-否
        // Map<String, Object> arguments
        Queue queue = new Queue("test.java.queue", true, false, false, null);
        amqpAdmin.declareQueue(queue);
        log.info("queue[{}]创建成功", queue.getName());
    }

    @Test
    public void testBinding() {
        // String destination:目的地名称,比如：队列的名称/交换机的名称
        // DestinationType destinationType:目的地类型
        // String exchange:交换机
        // String routingKey:绑定的routing key
        // Map<String, Object> arguments
        Binding binding = new Binding("test.java.queue",
                Binding.DestinationType.QUEUE, "test.java.exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("交换机[{}]与[{}]类型为[{}]绑定成功", binding.getExchange(), binding.getDestination(), binding.getDestinationType());
    }

    @Test
    public void testSendMessage() {
        String msg = "adasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd";
        rabbitTemplate.convertAndSend("test.java.exchange", "hello.java", msg);
        log.info("向交换机[{}]发送消息[{}]成功", "test.java.exchange", msg);
    }

    @Test
    public void testSendMessageConfirmCallback() {
    }

    @Before
    public void before() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 当前消息的唯一关联数据
             * @param ack 消息发送成功还是失败
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("correlationData[{}],ack[{}],cause[{}]", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

            /**
             *
             * @param message 投递失败的消息
             * @param replyCode 失败的状态码
             * @param replyText 失败的原因
             * @param exchange 失败的消息来自哪个交换机
             * @param routingKey 失败的消息来自哪个绑定key
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

            }
        });
    }
}
