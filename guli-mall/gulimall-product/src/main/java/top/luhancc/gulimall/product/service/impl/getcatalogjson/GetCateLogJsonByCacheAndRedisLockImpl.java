package top.luhancc.gulimall.product.service.impl.getcatalogjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
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
 * 加入缓存的基础上再次添加redis分布式锁来保证多个服务不会查询多次数据库
 *
 * @author luHan
 * @create 2021/1/7 10:08
 * @since 1.0.0
 */
@Service("getCateLogJsonByCacheAndRedisLock")
@Slf4j
public class GetCateLogJsonByCacheAndRedisLockImpl implements GetCateLogJsonService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<String, List<Category2Vo>> getCatalogJson() {
        String token = UUID.randomUUID().toString();
        // 1. 占分布式锁，去redis中设置一个key
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.SECONDS);
        if (lock != null && lock) {// 加锁成功
            // 设置锁的过期时间,但是这步之前可能会存在程序异常退出而无法执行到,
            // 推荐使用setIfAbsent(K key, V value, long timeout, TimeUnit unit)方法加锁
//            redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Category2Vo>> levelOneCategoryMap;
            try {
                levelOneCategoryMap = handleBusiness();
            } finally {
                //            redisTemplate.delete("lock");// 释放锁，可能会导致一个情况是删除了别的线程获取的redis锁
                // 删除锁的代码优化如下：先获取锁的value和自己的token比较是不是同一个，如果是同一个那么则进行删除锁
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if (token.equals(lockValue)) {
//                // 释放锁，还会有一种情况就是到达这条删锁命令时，
//                // redis里的锁的值变成了别的线程的token，这样还是会导致删除了别人的锁而出现问题
//                // 所以：判断是不是自己的锁+删除锁=原子操作
//                redisTemplate.delete("lock");
//            }
                // 删除锁的代码继续优化为使用lua脚本进行删除
                String deleteLockScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                        "then return redis.call(\"del\",KEYS[1]) " +
                        "else return 0 " +
                        "end ";
                Long deleteResult = redisTemplate.execute(new DefaultRedisScript<>(deleteLockScript, Long.class),
                        Collections.singletonList("lock"), token);
            }
            return levelOneCategoryMap;
        } else {
            // 加锁失败，进行重试
            return getCatalogJson();
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
