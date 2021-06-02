package top.luhancc.wanxin.finance.discover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author luHan
 * @create 2021/6/2 17:12
 * @since 1.0.0
 */
@SpringBootApplication
@EnableEurekaServer
public class WanXinFinanceDiscoverServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WanXinFinanceDiscoverServerApplication.class, args);
    }
}
