package top.luhancc.wanxin.finance.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author luHan
 * @create 2021/6/2 16:56
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("top.luhancc.wanxin.finance.account.mapper")
@ComponentScan({"top.luhancc.wanxin.finance.account", "org.dromara.hmily"})
public class WanXinFinanceAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(WanXinFinanceAccountApplication.class, args);
    }
}
