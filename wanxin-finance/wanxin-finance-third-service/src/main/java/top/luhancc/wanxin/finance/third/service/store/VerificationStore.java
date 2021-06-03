package top.luhancc.wanxin.finance.third.service.store;

/**
 * 验证信息存储 kv
 */
public interface VerificationStore{


    void set(String key, String value, Integer expire);


    String get(String key);


}
