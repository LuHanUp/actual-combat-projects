package top.luhancc.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author luHan
 * @create 2020/12/7 17:54
 * @since 1.0.0
 */
@MapperScan(value = "top.luhancc.gulimall.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
@EnableFeignClients
public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class, args);
    }
}
