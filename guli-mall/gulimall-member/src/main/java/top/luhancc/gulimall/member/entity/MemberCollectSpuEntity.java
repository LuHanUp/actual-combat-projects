package top.luhancc.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import top.luhancc.common.entity.BaseEntity;

/**
 * 会员收藏的商品
 * 
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
@Data
@TableName("ums_member_collect_spu")
public class MemberCollectSpuEntity extends BaseEntity {
	/**
	 * 会员id
	 */
	private Long memberId;
	/**
	 * spu_id
	 */
	private Long spuId;
	/**
	 * spu_name
	 */
	private String spuName;
	/**
	 * spu_img
	 */
	private String spuImg;
	/**
	 * create_time
	 */
	private Date createTime;

}
