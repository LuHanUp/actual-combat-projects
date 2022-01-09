package top.luahncc.payment.service;

import java.util.Map;

/**
 * @author luHan
 * @create 2022/1/9 21:03
 * @since 1.0.0
 */
public interface WxPayService {

    /**
     * 1. 生成订单
     * 2. 调用微信统一下单api
     * @param productId
     * @return
     */
    Map<String, Object> nativePay(Long productId);
}
