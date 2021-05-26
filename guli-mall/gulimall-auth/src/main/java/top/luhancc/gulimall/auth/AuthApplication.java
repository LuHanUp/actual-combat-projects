package top.luhancc.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import top.luhancc.common.component.HttpComponent;
import top.luhancc.common.config.CommonWebConfig;

@SpringBootApplication
@Import({HttpComponent.class, CommonWebConfig.class})
@EnableFeignClients
@EnableRedisHttpSession
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
