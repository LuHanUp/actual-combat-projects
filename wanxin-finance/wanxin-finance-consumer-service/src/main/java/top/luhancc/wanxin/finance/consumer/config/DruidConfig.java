package top.luhancc.wanxin.finance.consumer.config;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luHan
 * @create 2021/6/4 14:32
 * @since 1.0.0
 */
@Configuration
public class DruidConfig {

    /**
     * @return servlet registration bean
     * @description 注册一个StatViewServlet, 进行druid监控页面配置
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
        //先配置管理后台的servLet，访问的入口为/druid/
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(
                new StatViewServlet(), "/druid/*");
        // IP白名单 (没有配置或者为空，则允许所有访问)
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        // IP黑名单 (存在共同时，deny优先于allow)
        servletRegistrationBean.addInitParameter("deny", "");
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "sdb3309");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }
}
