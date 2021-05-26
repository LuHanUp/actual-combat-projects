package top.luhancc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:41:17
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

