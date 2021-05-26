package top.luhancc.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.luhancc.common.to.member.SocialUserTo;
import top.luhancc.gulimall.member.entity.MemberEntity;
import top.luhancc.gulimall.member.feign.CouponFeignService;
import top.luhancc.gulimall.member.service.MemberService;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.member.vo.MemberLoginVo;
import top.luhancc.gulimall.member.vo.MemberRegisterVo;


/**
 * 会员
 *
 * @author luHan
 * @email 765478939@qq.com
 * @date 2020-12-07 17:37:49
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        R memberCoupons = couponFeignService.memberCoupons();
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        return R.ok().put("member", memberEntity).put("coupons", memberCoupons.get("coupons"));
    }

    @PostMapping("/regist")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo) {
        return memberService.register(memberRegisterVo);
    }

    /**
     * 用户名和手机号  密码登录
     *
     * @param loginVo 登录信息
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo loginVo) {
        return memberService.login(loginVo);
    }

    /**
     * 社交登录
     *
     * @param socialUser
     * @return
     */
    @PostMapping("/social/login")
    public R socialLogin(@RequestBody SocialUserTo socialUser) {
        return memberService.socialLogin(socialUser);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
