package top.luhancc.gulimall.auth.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import top.luhancc.gulimall.auth.service.SmsCodeService;

/**
 * 测试环境发送验证码为123456
 *
 * @author luHan
 * @create 2021/1/11 15:17
 * @since 1.0.0
 */
@Primary
@Service
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
public class SmsCodeDevServiceImpl implements SmsCodeService {
    @Override
    public void sendCode(String phone, String code) {
        // noting to do
    }

    @Override
    public String getCode() {
        return "123456";
    }
}
