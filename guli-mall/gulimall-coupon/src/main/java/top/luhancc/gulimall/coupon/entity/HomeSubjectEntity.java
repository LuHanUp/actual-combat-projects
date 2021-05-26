package top.luhancc.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@Data
@TableName("sms_home_subject")
public class HomeSubjectEntity extends BaseEntity {
    /**
     * 专题名字
     */
    private String name;
    /**
     * 专题标题
     */
    private String title;
    /**
     * 专题副标题
     */
    private String subTitle;
    /**
     * 显示状态
     */
    private Integer status;
    /**
     * 详情连接
     */
    private String url;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 专题图片地址
     */
    private String img;

}
