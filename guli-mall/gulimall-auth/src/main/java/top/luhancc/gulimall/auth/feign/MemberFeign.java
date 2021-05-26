package top.luhancc.gulimall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.luhancc.common.to.member.SocialUserTo;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.auth.domain.vo.LoginVo;
import top.luhancc.gulimall.auth.domain.vo.MemberRegisterVo;

/**
 * @author luHan
 * @create 2021/1/12 11:10
 * @since 1.0.0
 */
@FeignClient(value = "gulimall-member")
public interface MemberFeign {

    @PostMapping("/member/member/login")
    public R login(@RequestBody LoginVo loginInfo);

    @PostMapping("/member/member/social/login")
    public R socialLogin(@RequestBody SocialUserTo socialUser);

    @PostMapping("/member/member/regist")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo);
}
