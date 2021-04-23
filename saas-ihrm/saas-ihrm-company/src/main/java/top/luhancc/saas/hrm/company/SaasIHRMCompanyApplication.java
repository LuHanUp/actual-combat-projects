package top.luhancc.saas.hrm.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import top.luhancc.hrm.common.utils.IdWorker;

/**
 * 公司服务的启动类
 * <p>
 * EntityScan:JPA扫描entity的包路径
 *
 * @author luHan
 * @create 2021/4/23 19:05
 * @since 1.0.0
 */
@SpringBootApplication
@EntityScan(basePackages = {"top.luhancc.saas.hrm.company.dao.entity"})
@Import({IdWorker.class})
public class SaasIHRMCompanyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasIHRMCompanyApplication.class, args);
    }
}
