package top.luhancc.gulimall.auth.service;

/**
 * @author luHan
 * @create 2021/1/11 15:33
 * @since 1.0.0
 */
public interface SmsCodeService {
    /**
     * 获取验证码
     *
     * @return
     */
    String getCode();

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    void sendCode(String phone, String code);
}
