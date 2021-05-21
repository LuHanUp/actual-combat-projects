package top.luhancc.saas.ihrm.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import top.luhancc.hrm.common.IhrmSpringBootApplication;

/**
 * @author luHan
 * @create 2021/5/21 10:26
 * @since 1.0.0
 */
@IhrmSpringBootApplication
@EntityScan(value = {"top.luhancc.saas.hrm.common.model.employee"})
@EnableEurekaClient
public class SaasIHRMEmployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasIHRMEmployeeApplication.class, args);
    }
}
