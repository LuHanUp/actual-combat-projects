package top.luhancc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

