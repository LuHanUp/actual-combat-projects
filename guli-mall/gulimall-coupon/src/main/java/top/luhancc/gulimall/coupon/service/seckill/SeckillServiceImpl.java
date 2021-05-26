package top.luhancc.gulimall.coupon.service.seckill;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.luhancc.common.bo.auth.MemberInfoBo;
import top.luhancc.common.utils.R;
import top.luhancc.common.to.coupon.SeckillOrderTo;
import top.luhancc.gulimall.coupon.domain.to.SeckillSkuRedisTo;
import top.luhancc.gulimall.coupon.domain.vo.SeckillSkuInfoVo;
import top.luhancc.gulimall.coupon.entity.SeckillSessionEntity;
import top.luhancc.gulimall.coupon.feign.ProductFeign;
import top.luhancc.gulimall.coupon.interceptor.LoginUserInterceptor;
import top.luhancc.gulimall.coupon.service.SeckillSessionService;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/1/20 13:46
 * @since 1.0.0
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillSessionService seckillSessionService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:skus:";// + 随机码

    @Override
    public void uploadSeckillSkuLatest3Day() {
        // 扫描最近三天需要参与秒杀的活动
        List<SeckillSessionEntity> sessions = seckillSessionService.getLast3DaySession();
        // 将商品信息添加到redis中
        if (!CollectionUtils.isEmpty(sessions)) {
            // 1. 缓存活动信息
            saveSessionInfo(sessions);
            // 2. 缓存活动关联的商品信息
            saveSessionSkuInfo(sessions);
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        // 1. 确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        for (String key : keys) {
            String keyCopy = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] s = keyCopy.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {
                // 2. 获取当前场次的商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                if (CollectionUtils.isEmpty(range)) {
                    continue;
                }
                BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> strings = hashOperations.multiGet(range);
                if (!CollectionUtils.isEmpty(strings)) {
                    return strings.stream().map(s1 -> JSON.parseObject(s1, SeckillSkuRedisTo.class)).collect(Collectors.toList());
                }
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        Set<String> keys = hashOperations.keys();
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }
        String regx = "\\d_" + skuId;
        for (String key : keys) {
            if (Pattern.matches(regx, key)) {
                String s = hashOperations.get(key);
                SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);
                long start = seckillSkuRedisTo.getStartTime();
                long end = seckillSkuRedisTo.getEndTime();
                long time = new Date().getTime();
                if (time >= start && time <= end) {
                } else {
                    seckillSkuRedisTo.setRandomCode(null);
                }
                return seckillSkuRedisTo;
            }
        }
        return null;
    }

    @Override
    public String seckill(String killId, String code, Integer num) {
        MemberInfoBo memberInfoBo = LoginUserInterceptor.loginUser.get();
        if (memberInfoBo == null) {
            log.warn("没有当前用户信息,请查看分布式session配置");
            return null;
        }

        // 获取秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String s = hashOperations.get(killId);
        if (StringUtils.isEmpty(s)) {
            log.warn("没有此秒杀商品:killId:{}", killId);
            return null;
        } else {
            SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);
            /*
                校验合法性
             */
            // 校验秒杀时间
            long start = seckillSkuRedisTo.getStartTime();
            long end = seckillSkuRedisTo.getEndTime();
            long time = new Date().getTime();
            if (time >= start && time <= end) {
                // 校验随机码
                String randomCode = seckillSkuRedisTo.getRandomCode();
                if (randomCode.equals(code)) {
                    // 校验商品id
                    String cacheKillId = seckillSkuRedisTo.getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuId();
                    if (cacheKillId.equals(killId)) {
                        // 验证购物数量是否合理
                        Integer seckillLimit = seckillSkuRedisTo.getSeckillLimit();
                        if (num <= seckillLimit) {
                            // 验证当前用户是否已经购买过：如果秒杀成功，就去redis中占位,key:userId_sessionId_skuId
                            String buyKey = memberInfoBo.getId() + "_" + seckillSkuRedisTo.getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuId();
                            long buyKeyTimeout = end - time;// buyKey缓存的过期时间
                            Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(buyKey, num.toString(), buyKeyTimeout, TimeUnit.MILLISECONDS);
                            if (ifAbsent != null && ifAbsent) {
                                // 预先扣减库存,使用分布式信号量来进行库存的扣减
                                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                                boolean acquire = semaphore.tryAcquire(num);
                                if (acquire) {// 秒杀成功
                                    // 快速下单
                                    String orderSn = IdWorker.getTimeId();
                                    // 发送mq消息
                                    SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                                    BeanUtils.copyProperties(seckillSkuRedisTo, seckillOrderTo);
                                    seckillOrderTo.setMemberId(memberInfoBo.getId());
                                    seckillOrderTo.setNum(num);
                                    rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill", seckillOrderTo);
                                    return orderSn;
                                } else {
                                    log.error("秒杀商品tryAcquire扣减库存失败");
                                    return null;
                                }
                            } else {
                                log.warn("用户[{}]已经参与过当前秒杀活动[{}]下的商品[{}]了", memberInfoBo.getId(), seckillSkuRedisTo.getPromotionSessionId(), seckillSkuRedisTo.getSkuId());
                                return null;
                            }
                        } else {
                            log.warn("秒杀该商品的数量不合理:num:{},seckillLimit:{}", num, seckillLimit);
                            return null;
                        }
                    } else {
                        log.warn("商品id不匹配:killId:{},cacheKillId:{}", killId, cacheKillId);
                        return null;
                    }
                } else {
                    log.warn("随机码不匹配:code:{},randomCode:{}", code, randomCode);
                    return null;
                }
            } else {
                log.warn("秒杀活动已经结束:{}", seckillSkuRedisTo);
                return null;
            }
        }
    }

    /**
     * 缓存活动信息
     *
     * @param sessions
     */
    private void saveSessionInfo(List<SeckillSessionEntity> sessions) {
        sessions.stream().forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;

            Boolean hasKey = redisTemplate.hasKey(key);
            if (!hasKey) {
                List<String> seckillSkuIds = session.getRelationSkuList().stream()
                        .map(sessionsSku -> session.getId() + "_" + sessionsSku.getSkuId())
                        .collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, seckillSkuIds);
            }
        });
    }

    /**
     * 缓存活动关联的商品信息
     *
     * @param sessions
     */
    private void saveSessionSkuInfo(List<SeckillSessionEntity> sessions) {
        sessions.stream().forEach(session -> {
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkuList().stream().forEach(seckillSkuInfo -> {
                String key = session.getId() + "_" + seckillSkuInfo.getSkuId().toString();
                Boolean hasKey = hashOperations.hasKey(key);
                if (!hasKey) {
                    SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                    seckillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                    seckillSkuRedisTo.setEndTime(session.getEndTime().getTime());

                    // 1. sku基本数据
                    R r = productFeign.getSkuInfoById(seckillSkuInfo.getSkuId());
                    if (r.isSuccess()) {
                        SeckillSkuInfoVo skuInfo = r.get("skuInfo", SeckillSkuInfoVo.class);
                        seckillSkuRedisTo.setSkuInfo(skuInfo);
                    }
                    // 2. sku秒杀信息
                    BeanUtils.copyProperties(seckillSkuInfo, seckillSkuRedisTo);
                    // 3. sku随机码?只有当秒杀开始的时候随机码才会暴露给前端页面，避免活动还没开始的时候会被恶意的请求秒杀接口
                    String token = UUID.randomUUID().toString().replace("-", "");
                    seckillSkuRedisTo.setRandomCode(token);

                    hashOperations.put(key, JSON.toJSONString(seckillSkuRedisTo));

                    // 使用库存作为分布式的信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(seckillSkuInfo.getSeckillCount().intValue());
                }
            });
        });
    }
}
