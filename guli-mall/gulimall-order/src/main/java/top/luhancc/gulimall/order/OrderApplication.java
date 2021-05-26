package top.luhancc.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 分布式事务
 *
 * @author luHan
 * @create 2020/12/7 17:51
 * @since 1.0.0
 */
@MapperScan(value = "top.luhancc.gulimall.order.dao")
@SpringBootApplication
@EnableRedisHttpSession
@EnableFeignClients
@EnableRabbit
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
