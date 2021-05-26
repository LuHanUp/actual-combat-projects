package top.luhancc.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

