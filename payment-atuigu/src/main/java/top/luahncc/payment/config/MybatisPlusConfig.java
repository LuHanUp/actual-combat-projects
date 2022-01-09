package top.luahncc.payment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MybatisPlus配置类
 *
 * @author luHan
 * @create 2022/1/9 18:54
 * @since 1.0.0
 */
@EnableTransactionManagement
@MapperScan(value = "top.luahncc.payment.mapper")
@Configuration
public class MybatisPlusConfig {
}
