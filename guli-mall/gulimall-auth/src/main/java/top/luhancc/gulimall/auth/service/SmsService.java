package top.luhancc.gulimall.auth.service;

import top.luhancc.common.utils.R;
import top.luhancc.gulimall.auth.domain.vo.LoginVo;
import top.luhancc.gulimall.auth.domain.vo.RegisterVo;

/**
 * @author luHan
 * @create 2021/1/11 15:17
 * @since 1.0.0
 */
public interface SmsService {
    /**
     * 发送短信验证码
     *
     * @param phone
     */
    void sendCode(String phone);


    R register(RegisterVo registerVo);

    R login(LoginVo loginVo);
}
