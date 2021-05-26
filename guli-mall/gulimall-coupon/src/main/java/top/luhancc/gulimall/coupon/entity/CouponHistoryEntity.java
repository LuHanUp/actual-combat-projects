package top.luhancc.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 优惠券领取历史记录
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@Data
@TableName("sms_coupon_history")
public class CouponHistoryEntity extends BaseEntity {
    /**
     * 优惠券id
     */
    private Long couponId;
    /**
     * 会员id
     */
    private Long memberId;
    /**
     * 会员名字
     */
    private String memberNickName;
    /**
     * 获取方式[0->后台赠送；1->主动领取]
     */
    private Integer getType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 使用状态[0->未使用；1->已使用；2->已过期]
     */
    private Integer useType;
    /**
     * 使用时间
     */
    private Date useTime;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 订单号
     */
    private Long orderSn;

}
