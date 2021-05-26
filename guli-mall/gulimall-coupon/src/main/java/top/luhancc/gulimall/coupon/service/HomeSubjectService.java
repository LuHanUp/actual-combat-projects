package top.luhancc.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.gulimall.coupon.entity.HomeSubjectEntity;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

