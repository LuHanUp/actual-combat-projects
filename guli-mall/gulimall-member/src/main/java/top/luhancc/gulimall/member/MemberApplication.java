package top.luhancc.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <pre>
 * 想要远程调用别的服务
 * 1. 引入open-feign
 * 2. 编写一个接口，告诉SpringCloud这个接口需要调用的远程服务名称
 *  2.1 声明接口的每一个方法都是调用哪个远程服务的哪个请求
 * 3. 开启远程调用功能
 * </pre>
 *
 * @author luHan
 * @create 2020/12/7 17:39
 * @since 1.0.0
 */
@MapperScan(value = "top.luhancc.gulimall.member.dao")
@SpringBootApplication
// 开启服务注册发现服务
@EnableDiscoveryClient
@EnableFeignClients(value = "top.luhancc.gulimall.member.feign")
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
