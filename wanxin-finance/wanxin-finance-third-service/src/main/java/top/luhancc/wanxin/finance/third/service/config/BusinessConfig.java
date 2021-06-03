package top.luhancc.wanxin.finance.third.service.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.luhancc.wanxin.finance.third.service.sms.handler.AbstractVerificationHandler;
import top.luhancc.wanxin.finance.third.service.sms.handler.SmsNumberVerificationHandler;
import top.luhancc.wanxin.finance.third.service.sms.qcloud.QCloudSmsService;
import top.luhancc.wanxin.finance.third.service.store.VerificationStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BusinessConfig {
    @Autowired
    private VerificationStore verificationStore;
    @Autowired
    private QCloudSmsService qCloudSmsService;

    @Bean
    public SmsNumberVerificationHandler smsNumberVerificationHandler() {
        SmsNumberVerificationHandler smsNumberVerificationHandler = new SmsNumberVerificationHandler("sms", 6);
        smsNumberVerificationHandler.setVerificationStore(verificationStore);
        smsNumberVerificationHandler.setSmsService(qCloudSmsService);
        return smsNumberVerificationHandler;
    }

    @Bean
    public Map<String, AbstractVerificationHandler> verificationHandlerMap() {
        List<AbstractVerificationHandler> verificationHandlerList = new ArrayList<>();
        verificationHandlerList.add(smsNumberVerificationHandler());

        Map<String, AbstractVerificationHandler> verificationHandlerMap = new HashMap<>();
        for (AbstractVerificationHandler handler : verificationHandlerList) {
            verificationHandlerMap.put(handler.getName(), handler);
        }
        return verificationHandlerMap;
    }

}
