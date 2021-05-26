package top.luhancc.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.common.to.member.SocialUserTo;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.member.entity.MemberEntity;
import top.luhancc.gulimall.member.vo.MemberLoginVo;
import top.luhancc.gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R register(MemberRegisterVo memberRegisterVo);

    R login(MemberLoginVo loginVo);

    /**
     * 社交登录
     *
     * @param socialUser 社交用户信息
     * @return
     */
    R socialLogin(SocialUserTo socialUser);
}

