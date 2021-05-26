package top.luhancc.gulimall.member.dao;

import top.luhancc.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
