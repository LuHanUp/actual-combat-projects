package top.luhancc.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 首页轮播广告
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@Data
@TableName("sms_home_adv")
public class HomeAdvEntity extends BaseEntity {
    /**
     * 名字
     */
    private String name;
    /**
     * 图片地址
     */
    private String pic;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 点击数
     */
    private Integer clickCount;
    /**
     * 广告详情连接地址
     */
    private String url;
    /**
     * 备注
     */
    private String note;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 发布者
     */
    private Long publisherId;
    /**
     * 审核者
     */
    private Long authId;

}
