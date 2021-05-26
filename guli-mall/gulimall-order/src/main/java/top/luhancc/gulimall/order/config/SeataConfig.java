package top.luhancc.gulimall.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author luHan
 * @create 2021/1/18 16:47
 * @since 1.0.0
 */
@Configuration
public class SeataConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        HikariDataSource hikariDataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            hikariDataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(hikariDataSource);
    }
}
