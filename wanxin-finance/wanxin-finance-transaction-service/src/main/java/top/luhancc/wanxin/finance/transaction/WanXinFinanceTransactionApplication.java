package top.luhancc.wanxin.finance.transaction;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"top.luhancc.wanxin.finance.transaction.feign"})
public class WanXinFinanceTransactionApplication {


    public static void main(String[] args) {
        SpringApplication.run(WanXinFinanceTransactionApplication.class, args);
    }


}

