package top.luhancc.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import top.luhancc.gulimall.coupon.config.SentinelConfig;

/**
 * <pre>
 * 如何使用nacos作为配置中心统一管理
 *  1. 引入spring-cloud-starter-alibaba-nacos-config依赖
 *  2. 创建一个bootstrap.yml文件,其中配置nacos地址：spring.cloud.nacos.config.server-addr=192.168.2.142:8848
 *  3. 需要在nacos配置中心添加一个数据集id为应用名称.properties的配置文件
 *  4. 动态刷新配置添加@RefreshScope
 *      如果配置中心和配置文件中配置了相同的配置项，优先使用配置中心配置
 *
 * nacos作为配置中心的一些细节
 *  1. 命名空间：主要是做配置隔离的，默认为public。
 *      1.1 以开发、测试、生产环境为例，可以创建对应环境的命名空间
 *      1.2 如果在程序中想要不同的命名空间的配置，可以在bootstrap.yml中使用`spring.cloud.nacos.config.namespace=命名空间的id`配置来实现
 *          2.1 基于每一个微服务之间互相隔离，以微服务为基准创建自己的命名空间
 *  2. 配置集：配置的集合
 *  3. 配置集id：Data ID，类似于文件名称
 *  4. 配置分组：默认分组为DEFAULT_GROUP
 *      使用`spring.cloud.nacos.config.group=分组名称`配置来使用不同的分组配置
 *      使用多个配置集
 *      1. 使用`spring.cloud.nacos.config.extension-configs[0].data-id=data id`
 *      `spring.cloud.nacos.config.extension-configs[0].group=配置分组`
 *      `spring.cloud.nacos.config.extension-configs[0].refresh=是否动态刷新`来实现使用多个配置集
 *
 * 整合sentinel
 *  1. 引入依赖`spring-cloud-starter-alibaba-sentinel`
 *  2. 下载sentinel控制台(这步可以下可以不下,看是否需要控制台界面)
 *  3. 配置连接控制台信息
 *      `spring.cloud.sentinel.transport.port=8719`
 *      `spring.cloud.sentinel.transport.dashboard=127.0.0.1:8333`
 *  4. 在控制台调整流控规则【默认是在项目运行的内存中】
 *  5. 如果需要在控制台看到实时监控的内容，需要整合actuator
 *      1. 引入依赖`spring-boot-starter-actuator`
 *      2. 在配置中设置`management.endpoints.web.exposure.include= '*' # 保留哪个监控接口`
 *  6. 自定义流控返回结果
 *      1. 给容器中注册一个`BlockExceptionHandler`bean即可{@link SentinelConfig#blockExceptionHandler()}
 *  7. 使用sentinel对Feign调用进行流控
 *      1. 配置`feign.sentinel.enabled: true`设置为true
 *      2. FeignClient注解上添加一个Fallback类即可`@FeignClient(value = "gulimall-product", fallback = ProductFallback.class)`
 *          {@link top.luhancc.gulimall.coupon.feign.fallback.ProductFallback}
 *      3.
 *  8. 自定义受保护的资源,参考sentinel文档{https://github.com/alibaba/Sentinel/wiki/%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8#%E5%AE%9A%E4%B9%89%E8%B5%84%E6%BA%90}
 *      1. 代码方式：
 *          {@code
 *              // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
 *              try (Entry entry = SphU.entry("resourceName")) {
 *                  // 被保护的业务逻辑
 *                  // do something here...
 *              } catch (BlockException ex) {
 *                  // 资源访问阻止，被限流或被降级
 *                  // 在此处进行相应的处理操作
 *              }
 *          }
 *      2. 注解方式:
 *          {@code
 *          // 原本的业务方法.
 *          @SentinelResource(blockHandler = "blockHandlerForGetUser")
 *          public User getUserById(String id) {
 *              throw new RuntimeException("getUserById command failed");
 *          }
 *          // blockHandler 函数，原方法调用被限流/降级/系统保护的时候调用
 *          public User blockHandlerForGetUser(String id, BlockException ex) {
 *              return new User("admin");
 *          }
 *          }
 *  9. gateway层添加sentinel流控，参考{https://github.com/alibaba/Sentinel/wiki/%E7%BD%91%E5%85%B3%E9%99%90%E6%B5%81}
 *      1. gateway模块添加依赖`spring-cloud-alibaba-sentinel-gateway`
 *
 * 整合zipkin+seluth
 *  1. 添加zipkin依赖`spring-cloud-starter-zipkin`
 *  2. 添加seluth依赖`spring-cloud-starter-sleuth`(因为zipkin会添加seluth依赖,所以这一步可以省略)
 *  3. 添加配置
 *      {@code
 *      spring:
 *          zipkin:
 *              base-url: http://192.168.2.142:9411/ # zipkin服务器地址
 *              discovery-client-enabled: false # 关闭服务发现,否则SpringCloud会把zipkin的url当做服务名注册进注册中心
 *              sender:
 *                  type: web # 设置使用http的方式传输数据
 *          sleuth:
 *              sampler:
 *                  probability: 1 # 设置抽样采集率为100% 默认为0.1即10%
 *      }
 * </pre>
 *
 * @author luHan
 * @create 2020/12/7 17:23
 * @since 1.0.0
 */
@MapperScan(value = "top.luhancc.gulimall.coupon.dao")
@SpringBootApplication
// 开启服务注册发现服务
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
}
