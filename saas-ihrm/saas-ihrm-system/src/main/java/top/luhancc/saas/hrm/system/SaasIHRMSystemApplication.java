package top.luhancc.saas.hrm.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import top.luhancc.hrm.common.IhrmSpringBootApplication;

/**
 * 系统管理服务启动类
 *
 * @author luHan
 * @create 2021/5/13 15:23
 * @since 1.0.0
 */
@IhrmSpringBootApplication
@EntityScan(value = {"top.luhancc.saas.hrm.common.model.system"})
public class SaasIHRMSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasIHRMSystemApplication.class, args);
    }
}
