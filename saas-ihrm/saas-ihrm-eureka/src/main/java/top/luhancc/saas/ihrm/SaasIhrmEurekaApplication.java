package top.luhancc.saas.ihrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author luHan
 * @create 2021/5/19 14:34
 * @since 1.0.0
 */
@EnableEurekaServer
@SpringBootApplication
public class SaasIhrmEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasIhrmEurekaApplication.class, args);
    }
}
