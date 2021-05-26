package top.luhancc.gulimall.coupon.scheduled;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 1. @EnableScheduling 开启定时任务
 * 2. @Scheduled 设置一个执行定时任务的方法
 *
 * @author luHan
 * @create 2021/1/20 13:28
 * @since 1.0.0
 */
@EnableScheduling
@Component
public class HelloSchedule {

    /**
     * <pre>
     * 1. Spring中这个cron只有6位组成
     * 2. Spring中周一~周日 就是1~7
     * 3. Spring中定时任务默认是阻塞的
     *      1. 使用CompletableFuture异步执行
     *      2. Spring支持定时任务线程池：
     *          1. 参照{@link org.springframework.boot.autoconfigure.task.TaskSchedulingProperties}进行配置即可 【容易出现问题】
     *      3. 让方法异步执行
     *          1. @EnableAsync
     *          2. 方法上标注@Async
     *          3. 异步任务的配置参考{@link org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration}
     *              其中属性配置参考{@link org.springframework.boot.autoconfigure.task.TaskExecutionProperties}
     * </pre>
     */
    @Scheduled(cron = "* * * * * ?")
    public void hello() {
    }
}
