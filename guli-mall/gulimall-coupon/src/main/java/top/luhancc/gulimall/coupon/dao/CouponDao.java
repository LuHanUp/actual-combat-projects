package top.luhancc.gulimall.coupon.dao;

import top.luhancc.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
