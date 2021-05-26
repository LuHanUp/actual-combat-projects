package top.luhancc.gulimall.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.auth.domain.vo.LoginVo;
import top.luhancc.gulimall.auth.domain.vo.MemberRegisterVo;
import top.luhancc.gulimall.auth.domain.vo.RegisterVo;
import top.luhancc.gulimall.auth.feign.MemberFeign;
import top.luhancc.gulimall.auth.service.SmsCodeService;
import top.luhancc.gulimall.auth.service.SmsService;

import java.util.concurrent.TimeUnit;

/**
 * 真实的向手机号发送短信验证码
 *
 * @author luHan
 * @create 2021/1/11 15:17
 * @since 1.0.0
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private SmsCodeService smsCodeService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeign memberFeign;

    @Override
    public void sendCode(String phone) {
        // 验证码60s内只能发送一个
        Boolean hasKey = redisTemplate.hasKey("sms:code:" + phone + "_time");
        if (hasKey != null && hasKey) {
            throw new UnsupportedOperationException("不能重复发送验证码,请在60s后重新发送");
        }
        String code = smsCodeService.getCode();
        // 设置验证码的过期时间
        redisTemplate.opsForValue().set("sms:code:" + phone, code, 10, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("sms:code:" + phone + "_time", phone, 60, TimeUnit.SECONDS);
        // 发送验证码
        smsCodeService.sendCode(phone, code);
    }

    @Override
    public R register(RegisterVo registerVo) {
        // 验证验证码是否正确
        String code = redisTemplate.opsForValue().get("sms:code:" + registerVo.getPhone());
        if (!registerVo.getCode().equals(code)) {
            return R.error("验证码不正确");
        }
        // 删除验证码
        redisTemplate.delete("sms:code:" + registerVo.getPhone());
        // 调用远程服务完成注册
        MemberRegisterVo memberRegisterVo = new MemberRegisterVo();
        memberRegisterVo.setPassword(registerVo.getPassword());
        memberRegisterVo.setUserName(registerVo.getUsername());
        memberRegisterVo.setPhone(registerVo.getPhone());
        return memberFeign.register(memberRegisterVo);
    }

    @Override
    public R login(LoginVo loginVo) {
        return memberFeign.login(loginVo);
    }
}
