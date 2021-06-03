package top.luhancc.wanxin.finance.third.service.sms.generator;

import java.util.UUID;

public class UUIDVerificationKeyGenerator implements VerificationKeyGenerator {
    @Override
    public String generate(String prefix) {
        String uuid = UUID.randomUUID().toString();
        return prefix + uuid.replaceAll("-", "");
    }
}
