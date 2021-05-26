package top.luhancc.gulimall.coupon.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.luhancc.gulimall.coupon.service.seckill.SeckillService;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架任务
 * 1. 每天晚上3点上架最近3天需要参与秒杀的商品
 * 需要关注的时间段为:
 * 1. 当天00:00:00 - 23:59:59
 * 2. 明天00:00:00 - 23:59:59
 * 3. 后天00:00:00 - 23:59:59
 *
 * @author luHan
 * @create 2021/1/20 13:42
 * @since 1.0.0
 */
@EnableScheduling
@EnableAsync
@Slf4j
public class SeckillSkuScheduled {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;

    private final String UPLOAD_LOCK = "seckill:upload:lock";

    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    public void uploadSeckillSkuLatest3Day() {
        // 加一个分布式锁来保证任务只有一个线程可以执行
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            log.info("上架秒杀商品任务开始");
            seckillService.uploadSeckillSkuLatest3Day();
            log.info("上架秒杀商品任务结束");
        } finally {
            lock.unlock();
        }
    }
}
