package top.luhancc.wanxin.finance.consumer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luHan
 * @create 2021/6/4 13:45
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("top.luhancc.wanxin.finance.consumer.mapper")
public class WanXinFinanceConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WanXinFinanceConsumerApplication.class, args);
    }
}
