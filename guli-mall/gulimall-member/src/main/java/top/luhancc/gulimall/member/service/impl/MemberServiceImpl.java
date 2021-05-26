package top.luhancc.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.luhancc.common.to.member.SocialUserTo;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.member.dao.MemberDao;
import top.luhancc.gulimall.member.entity.MemberEntity;
import top.luhancc.gulimall.member.entity.MemberLevelEntity;
import top.luhancc.gulimall.member.service.MemberLevelService;
import top.luhancc.gulimall.member.service.MemberService;
import top.luhancc.gulimall.member.vo.MemberLoginVo;
import top.luhancc.gulimall.member.vo.MemberRegisterVo;

import java.util.Date;
import java.util.Map;


@Service("memberService")
@Slf4j
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberLevelService memberLevelService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R register(MemberRegisterVo memberRegisterVo) {
        // 检查用户名和手机号是否唯一
        if (checkUserName(memberRegisterVo.getUserName())) {
            return R.error("用户名已存在");
        }
        if (checkPhone(memberRegisterVo.getPhone())) {
            return R.error("手机号码已存在");
        }
        MemberEntity memberEntity = new MemberEntity();
        // 设置默认等级
        MemberLevelEntity memberLevel = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevel.getId());
        memberEntity.setUsername(memberRegisterVo.getUserName());
//        String salt = UUID.randomUUID().toString().substring(0, 9);
        // 使用BCryptPasswordEncoder进行加密
        memberEntity.setPassword(passwordEncoder.encode(memberRegisterVo.getPassword()));
//        memberEntity.setSalt(salt);
        memberEntity.setMobile(memberRegisterVo.getPhone());
        memberEntity.setSourceType(1);
        memberEntity.setIntegration(0);
        memberEntity.setGrowth(0);
        memberEntity.setStatus(1);
        memberEntity.setCreateTime(new Date());

        this.save(memberEntity);
        return R.ok();
    }

    @Override
    public R login(MemberLoginVo loginVo) {
        String useracct = loginVo.getUseracct();
        // 查询出用户
        LambdaQueryWrapper<MemberEntity> queryWrapper = Wrappers.lambdaQuery(MemberEntity.class)
                .eq(MemberEntity::getMobile, useracct)
                .or().eq(MemberEntity::getUsername, useracct);
        MemberEntity memberEntity = this.getOne(queryWrapper);
        if (memberEntity == null) {
            log.warn("用户不存在,{}", loginVo);
            return R.error("用户名或密码不正确");
        }
        boolean matches = passwordEncoder.matches(loginVo.getPassword(), memberEntity.getPassword());
        if (!matches) {
            log.warn("用户密码错误,{}", loginVo);
            return R.error("用户名或密码不正确");
        }
        // 登录成功将用户信息进行返回
        return R.ok(memberEntity);
    }

    /**
     * 社交登录
     * 其中包含登录和注册，如果社交用户是一次进入网站，那么需要进行注册业务
     * 否则直接登录
     *
     * @param socialUser 社交用户信息
     * @return
     */
    @Override
    public R socialLogin(SocialUserTo socialUser) {
        String account = socialUser.getAccount();
        LambdaQueryWrapper<MemberEntity> queryWrapper = Wrappers.lambdaQuery(MemberEntity.class)
                .eq(MemberEntity::getAccount, account);
        MemberEntity memberEntity = this.getOne(queryWrapper);
        if (memberEntity != null) {
            log.info("社交用户注册过,直接进行登录:{}", socialUser);
            // 说明这个社交用户注册过
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpireTime(socialUser.getExpireTime());
            this.updateById(memberEntity);
            return R.ok().put("data", memberEntity);
        } else {
            log.info("社交用户没有注册,直接进行注册,再进行登录:{}", socialUser);
            // 执行注册
            MemberLevelEntity memberLevel = memberLevelService.getDefaultLevel();
            MemberEntity newMember = new MemberEntity();
            newMember.setLevelId(memberLevel.getId());
            newMember.setNickname(socialUser.getNickName());
            newMember.setHeader(socialUser.getHeadImg());
            newMember.setGender(socialUser.getGender());
            newMember.setSourceType(socialUser.getSourceType().getType());
            newMember.setIntegration(0);
            newMember.setGrowth(0);
            newMember.setStatus(1);
            newMember.setCreateTime(new Date());
            newMember.setAccount(socialUser.getAccount());
            newMember.setAccessToken(socialUser.getAccessToken());
            newMember.setExpireTime(socialUser.getExpireTime());

            this.save(newMember);
            return R.ok().put("data", newMember);
        }
    }

    /**
     * 检查手机号码是否存在
     *
     * @param phone 手机号码
     * @return true:存在 false:不存在
     */
    private boolean checkPhone(String phone) {
        LambdaQueryWrapper<MemberEntity> queryWrapper = Wrappers.lambdaQuery(MemberEntity.class)
                .eq(MemberEntity::getMobile, phone);
        MemberEntity memberEntity = this.getOne(queryWrapper);
        return memberEntity != null;
    }

    /**
     * 检查用户名称是否存在
     *
     * @param userName 用户名称
     * @return true:存在 false:不存在
     */
    private boolean checkUserName(String userName) {
        LambdaQueryWrapper<MemberEntity> queryWrapper = Wrappers.lambdaQuery(MemberEntity.class)
                .eq(MemberEntity::getUsername, userName);
        MemberEntity memberEntity = this.getOne(queryWrapper);
        return memberEntity != null;
    }

}