package top.luhancc.hrm.common;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.CrossOrigin;
import top.luhancc.hrm.common.config.CrossOriginConfig;
import top.luhancc.hrm.common.config.JpaConfig;
import top.luhancc.hrm.common.config.JwtAuthorizationConfig;
import top.luhancc.hrm.common.config.ShiroAuthorizationConfig;
import top.luhancc.hrm.common.exception.GlobalExceptionHandler;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.hrm.common.utils.JwtUtils;

import java.lang.annotation.*;

/**
 * @author luHan
 * @create 2021/4/25 09:59
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
@CrossOrigin // 解决跨域问题
@EnableConfigurationProperties({JwtUtils.class})
@Import({JpaConfig.class, IdWorker.class, GlobalExceptionHandler.class, CrossOriginConfig.class, JwtAuthorizationConfig.class, ShiroAuthorizationConfig.class})
public @interface IhrmSpringBootApplication {

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     *
     * @return the classes to exclude
     */
    @AliasFor(annotation = EnableAutoConfiguration.class)
    Class<?>[] exclude() default {};

    /**
     * Exclude specific auto-configuration class names such that they will never be
     * applied.
     *
     * @return the class names to exclude
     * @since 1.3.0
     */
    @AliasFor(annotation = EnableAutoConfiguration.class)
    String[] excludeName() default {};

    /**
     * Base packages to scan for annotated components. Use {@link #scanBasePackageClasses}
     * for a type-safe alternative to String-based package names.
     *
     * @return base packages to scan
     * @since 1.3.0
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    /**
     * Type-safe alternative to {@link #scanBasePackages} for specifying the packages to
     * scan for annotated components. The package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that
     * serves no purpose other than being referenced by this attribute.
     *
     * @return base packages to scan
     * @since 1.3.0
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};

}
