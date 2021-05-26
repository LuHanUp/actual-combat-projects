package top.luhancc.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 专题商品
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:15:42
 */
@Data
@TableName("sms_home_subject_spu")
public class HomeSubjectSpuEntity extends BaseEntity {
    /**
     * 专题名字
     */
    private String name;
    /**
     * 专题id
     */
    private Long subjectId;
    /**
     * spu_id
     */
    private Long spuId;
    /**
     * 排序
     */
    private Integer sort;

}
