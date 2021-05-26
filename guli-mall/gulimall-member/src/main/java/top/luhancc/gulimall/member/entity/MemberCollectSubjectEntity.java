package top.luhancc.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 会员收藏的专题活动
 * 
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
@Data
@TableName("ums_member_collect_subject")
public class MemberCollectSubjectEntity extends BaseEntity {
	/**
	 * subject_id
	 */
	private Long subjectId;
	/**
	 * subject_name
	 */
	private String subjectName;
	/**
	 * subject_img
	 */
	private String subjectImg;
	/**
	 * 活动url
	 */
	private String subjectUrll;

}
