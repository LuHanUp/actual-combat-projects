package top.luhancc.hrm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * @author luHan
 * @create 2021/5/17 13:55
 * @since 1.0.0
 */
@Configuration
public class JpaConfig {

    /**
     * 解决jpa中的no session问题
     *
     * @return
     */
    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }
}
