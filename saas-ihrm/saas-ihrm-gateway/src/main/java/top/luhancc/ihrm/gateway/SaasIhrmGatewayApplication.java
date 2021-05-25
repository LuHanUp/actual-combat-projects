package top.luhancc.ihrm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author luHan
 * @create 2021/5/25 15:06
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy // 开启zull组件网关功能
public class SaasIhrmGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasIhrmGatewayApplication.class, args);
    }
}
