package top.luhancc.saas.hrm.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import top.luhancc.hrm.common.IhrmSpringBootApplication;

/**
 * 公司服务的启动类
 * <p>
 * EntityScan:JPA扫描entity的包路径
 *
 * @author luHan
 * @create 2021/4/23 19:05
 * @since 1.0.0
 */
@IhrmSpringBootApplication
@EntityScan(value = {"top.luhancc.saas.hrm.common.model.company"})
@EnableEurekaClient
public class SaasIHRMCompanyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasIHRMCompanyApplication.class, args);
    }
}
