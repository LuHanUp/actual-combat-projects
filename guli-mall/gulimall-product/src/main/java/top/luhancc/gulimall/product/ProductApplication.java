package top.luhancc.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * <pre>
 * 整合MyBatis-Plus
 *  1、导入依赖
 * {@code
 *      <dependency>
 *          <groupId>com.baomidou</groupId>
 *          <artifactId>mybatis-plus-boot-starter</artifactId>
 *          <version>3.2.0</version>
 *      </dependency>
 * }
 *  2、配置
 *      1)、配置数据源；
 *      2）、导入数据库的驱动。https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-versions.html
 *      3）、在application.yml配置数据源相关信息
 *  3、配置MyBatis-Plus；
 *      1）、使用@MapperScan
 *      2）、告诉MyBatis-Plus，sql映射文件位置
 *
 *  4、Mybatis-plus逻辑删除
 *      1）、配置全局的逻辑删除规则（这个配置可以省略）
 *      2）、配置逻辑删除的组件Bean（3.1.x后面的可以不添加这个Bean）
 *      3）、给Entity的逻辑删除字段加上逻辑删除注解@TableLogic
 *
 * JSR303
 *  1）、给Bean添加校验注解:javax.validation.constraints，并定义自己的message提示
 *  2)、开启校验功能@Valid
 *      效果：校验错误以后会有默认的响应；
 *  3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
 *  4）、分组校验（多场景的复杂校验）
 *      1)、	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
 *      给校验注解标注什么情况需要进行校验
 *      2）、@Validated({AddGroup.class})
 *      3)、默认没有指定分组的校验注解@NotBlank，在分组校验情况@Validated({AddGroup.class})下不生效，只会在@Validated生效；
 *
 *  5）、自定义校验
 *      1）、编写一个自定义的校验注解
 *      2）、编写一个自定义的校验器 ConstraintValidator
 *      3）、关联自定义的校验器和自定义的校验注解
 * {@code
 *      @Documented
 *      @Constraint(validatedBy = { ListValueConstraintValidator.class【可以指定多个不同的校验器，适配不同类型的校验】 })
 *      @Target(value = { METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 *      @Retention(RUNTIME)
 *      public @interface ListValue {}
 * }
 *
 * 统一的异常处理{@code @ControllerAdvice}
 *  1）、编写异常处理类，使用@ControllerAdvice。
 *  2）、使用@ExceptionHandler标注方法可以处理的异常。
 *
 * 模板引擎
 *  1. 页面修改不重启服务器实时更新(如果是代码配置，还是重启下服务)
 *  2. 添加dev-tools依赖
 * {@code
 *      <dependency>
 *          <groupId>org.springframework.boot</groupId>
 *          <artifactId>spring-boot-devtools</artifactId>
 *          <optional>true</optional>
 *      </dependency>
 * }
 *  3. 修改完页面需要重新编译即可
 *
 * 整合redis
 *  1. 引入data-redis-starter
 *  2. 配置redis的相关配置信息
 *      `spring.redis.host=redis的ip地址`
 *  3. 基础的使用StringRedisTemplate即可
 *
 * 整个Redisson作为分布式锁的实现
 *  1. 引入redisson依赖
 *  2. 配置Redisson 参考{@link top.luhancc.gulimall.product.config.RedissonConfig}
 *
 * </pre>
 *
 * @create 2020/12/7 17:44
 * @since 1.0.0
 */
@MapperScan(value = "top.luhancc.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
