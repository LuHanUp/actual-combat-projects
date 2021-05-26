package top.luhancc.gulimall.product.service.impl.getcatalogjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import top.luhancc.gulimall.product.dao.CategoryDao;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.service.GetCateLogJsonService;
import top.luhancc.gulimall.product.vo.web.Category2Vo;
import top.luhancc.gulimall.product.vo.web.Category3Vo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 使用Redisson实现分布式锁获取分类数据
 *
 * @author luHan
 * @create 2021/1/7 10:08
 * @since 1.0.0
 */
@Service("getCateLogJsonByRedissonLock")
@Slf4j
public class GetCateLogJsonByRedissonLockImpl implements GetCateLogJsonService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redisson;

    @Override
    public Map<String, List<Category2Vo>> getCatalogJson() {
        RLock lock = redisson.getLock("lock-redisson");
        lock.lock();// 阻塞式等待，默认加的锁都是30s时间
        // 1. 锁的自动续期：如果业务超长，运行期间自动给锁续上新的过期时间【看门狗默认的续期时间是30s】
        // 2. 加锁的业务只要运行完成，解锁之后，看门狗就不会再给锁进行续期

//        lock.lock(10, TimeUnit.SECONDS);// 10秒后自动结果，调用这个方法redisson不会自动给锁进行续期，所以注意一定要大于业务的执行时间
        // 1. 如果传递了锁的过期时间，就直接发送加锁脚本给redis执行，然后返回
        // 2. 如果没有指定过期时间
        //  1. 先设置默认的过期时间30s
        //  2. 启动一个定时任务(TimeTask)，每个internalLockLeaseTime / 3的时间执行一次锁的续期操作
        // 最佳实战：如果能明确业务执行的大致时间，就可以使用带过期时间的lock方法，从而避免自动续期任务的重复执行
        // 加锁成功
        try {
            return handleBusiness();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行获取分页的业务代码
     *
     * @return
     */
    private Map<String, List<Category2Vo>> handleBusiness() {
        /*
            使用缓存进行优化
            可能会产生堆外溢出异常：OutOfDirectMemoryError
            产生的原因如下：
            1. springboot-redis2.0以后默认是使用lettuce作为操作redis的客户端。其中lettuce使用netty进行网络通信
            2. 主要就是lettuce的bug导致堆外内存溢出 如果没有指定堆外内存，netty就会默认使用-Xmx指定的内存
                可以通过-Dio.netty.maxDirectMemory进行设置
            解决方案：不能光使用-Dio.netty.maxDirectMemory调大内存
            1. 升级lettuce客户端版本
            2. 切换使用jedis
                1. 在spring-boot-starter-data-redis依赖依赖中排除lettuce
                2. 添加jedis依赖
            redisTemplate的生成参考org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration

            缓存穿透：大量访问缓存中不存在的key，从而导致大量的请求落到数据库上，压垮数据库
                解决方案：放置空的结果在缓存中，并设置过期时间
            缓存雪崩：缓存中大量的key在同一时间失效，而导致大量的请求落到了数据库上
                解决方案：在原有的过期时间上追加随机的时间，使得过期时间重复率降低
            缓存击穿：大量访问一个热点的key，并且当这个热点的key失效后，请求落到了数据库上
                解决方案：在请求前加锁，如果是请求数据库就先加锁，获得结果后将数据存放入缓存中，后序的请求直接查询缓存即可
        */
        Boolean cacheHashData = redisTemplate.hasKey("categoryJson");
        if (cacheHashData != null && cacheHashData) {
            String categoryJson = redisTemplate.opsForValue().get("categoryJson");
            log.info("数据从缓存中获取");
            return JSON.parseObject(categoryJson, new TypeReference<Map<String, List<Category2Vo>>>() {
            });
        }
        List<CategoryEntity> categoryEntities = categoryDao.selectList(null);
        // 1. 获取所有的一级分类
        List<CategoryEntity> levelOneCategories = getByParentId(categoryEntities, 0L);
        // 2. 封装数据
        Map<String, List<Category2Vo>> levelOneCategoryMap = levelOneCategories.stream().collect(Collectors.toMap(
                key -> key.getCatId().toString(),
                value -> {
                    // 查询每个一级分类的二级分类
                    List<CategoryEntity> levelTwoCategories = getByParentId(categoryEntities, value.getCatId());
                    // 将CategoryEntity封装为Category2Vo
                    List<Category2Vo> category2VoList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(levelTwoCategories)) {
                        category2VoList = levelTwoCategories.stream().map(category2 -> {
                            // 获取2级分类的三级分类
                            List<CategoryEntity> levelThreeCategories = getByParentId(categoryEntities, category2.getCatId());
                            List<Category3Vo> category3VoList = new ArrayList<>();
                            if (CollectionUtils.isNotEmpty(levelThreeCategories)) {
                                category3VoList = levelThreeCategories.stream().map(category3 -> {
                                    return Category3Vo.builder()
                                            .catalog2Id(category2.getCatId().toString())
                                            .id(category3.getCatId().toString())
                                            .name(category3.getName())
                                            .build();
                                }).collect(Collectors.toList());
                            }
                            return Category2Vo.builder()
                                    .catalog1Id(value.getCatId().toString())
                                    .catalog3List(category3VoList)
                                    .id(category2.getCatId().toString())
                                    .name(category2.getName())
                                    .build();
                        }).collect(Collectors.toList());
                    }
                    return category2VoList;
                }));
        // 存放入缓存中
        redisTemplate.opsForValue().set("categoryJson", JSON.toJSONString(levelOneCategoryMap));
        return levelOneCategoryMap;
    }

    /**
     * 从categoryEntities中获取和parentId相同的CategoryEntity
     *
     * @param categoryEntities 需要筛选的分类列表
     * @param parentId         父分类id
     * @return 和parentId相同的CategoryEntity列表
     */
    private List<CategoryEntity> getByParentId(List<CategoryEntity> categoryEntities, Long parentId) {
        return categoryEntities.stream().filter(item -> item.getParentCid().equals(parentId)).collect(Collectors.toList());
    }
}
