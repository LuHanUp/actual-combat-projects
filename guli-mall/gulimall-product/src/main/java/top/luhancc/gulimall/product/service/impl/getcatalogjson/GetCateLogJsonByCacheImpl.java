package top.luhancc.gulimall.product.service.impl.getcatalogjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.luhancc.gulimall.product.dao.CategoryDao;
import top.luhancc.gulimall.product.entity.CategoryEntity;
import top.luhancc.gulimall.product.service.GetCateLogJsonService;
import top.luhancc.gulimall.product.vo.web.Category2Vo;
import top.luhancc.gulimall.product.vo.web.Category3Vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 将数据存入缓存中，没有再从数据库查找
 * <p>性能还可以</p>
 *
 * @author luHan
 * @create 2021/1/7 10:08
 * @since 1.0.0
 */
@Service("getCateLogJsonByCache")
@Slf4j
public class GetCateLogJsonByCacheImpl implements GetCateLogJsonService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<String, List<Category2Vo>> getCatalogJson() {
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
