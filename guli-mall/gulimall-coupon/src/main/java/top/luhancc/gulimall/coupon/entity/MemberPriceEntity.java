package top.luhancc.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

import java.math.BigDecimal;

/**
 * 商品会员价格
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@Data
@TableName("sms_member_price")
public class MemberPriceEntity extends BaseEntity {
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * 会员等级id
     */
    private Long memberLevelId;
    /**
     * 会员等级名
     */
    private String memberLevelName;
    /**
     * 会员对应价格
     */
    private BigDecimal memberPrice;
    /**
     * 可否叠加其他优惠[0-不可叠加优惠，1-可叠加]
     */
    private Integer addOther;

}
