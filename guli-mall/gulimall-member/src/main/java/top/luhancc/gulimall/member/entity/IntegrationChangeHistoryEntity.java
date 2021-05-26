package top.luhancc.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 积分变化历史记录
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
@Data
@TableName("ums_integration_change_history")
public class IntegrationChangeHistoryEntity extends BaseEntity {
    /**
     * member_id
     */
    private Long memberId;
    /**
     * create_time
     */
    private Date createTime;
    /**
     * 变化的值
     */
    private Integer changeCount;
    /**
     * 备注
     */
    private String note;
    /**
     * 来源[0->购物；1->管理员修改;2->活动]
     */
    private Integer sourceTyoe;

}
