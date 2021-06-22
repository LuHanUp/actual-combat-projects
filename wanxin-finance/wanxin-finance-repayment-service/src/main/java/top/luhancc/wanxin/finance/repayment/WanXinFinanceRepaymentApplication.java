package top.luhancc.wanxin.finance.repayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author luHan
 * @create 2021/6/22 15:28
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"org.dromara.hmily", "top.luhancc.wanxin.finance.repayment"}, exclude = {
        MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"top.luhancc.wanxin.finance.repayment.feign"})
public class WanXinFinanceRepaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(WanXinFinanceRepaymentApplication.class, args);
    }
}
