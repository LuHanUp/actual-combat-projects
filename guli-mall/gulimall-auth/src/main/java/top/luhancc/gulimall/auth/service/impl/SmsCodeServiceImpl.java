package top.luhancc.gulimall.auth.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import top.luhancc.gulimall.auth.service.SmsCodeService;

import java.util.UUID;

/**
 * 真实环境向短信验证码服务商发送验证码
 *
 * @author luHan
 * @create 2021/1/11 15:17
 * @since 1.0.0
 */
@Service
public class SmsCodeServiceImpl implements SmsCodeService {
    @Override
    public void sendCode(String phone, String code) {
        //TODO 向短信码服务商发送
    }

    @Override
    public String getCode() {
        return UUID.randomUUID().toString().substring(0, 5);
    }
}
