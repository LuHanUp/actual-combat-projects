package top.luhancc.wanxin.finance.account.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.account.service.SmsService;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.util.OkHttpUtil;

/**
 * @author luHan
 * @create 2021/6/3 10:29
 * @since 1.0.0
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Value("${sms.url}")
    private String smsUrl;

    @Override
    public RestResponse getSmsCode(String mobile) {
        return OkHttpUtil.post(smsUrl + "/generate?effectiveTime=300&name=sms", "{\"mobile\":\"" + mobile + "\"}");
    }

    @Override
    public boolean verifyCode(String key, String code) {
        RestResponse restResponse = OkHttpUtil.post(smsUrl + "/verify?name=sms&verificationKey=" + key + "&verificationCode=" + code, "");
        if (restResponse.isSuccessful()) {
            Object result = restResponse.getResult();
            return Boolean.parseBoolean(result.toString());
        }
        return false;
    }
}
