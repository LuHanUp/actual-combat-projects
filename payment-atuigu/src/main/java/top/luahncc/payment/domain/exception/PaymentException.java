package top.luahncc.payment.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author luHan
 * @create 2022/1/9 17:41
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum PaymentException implements BaseException {
    INTERNAL_ERROR("500", "内部错误,请联系管理员!!!"),

    // 1xxxx  商品相关的错误

    // 2xxxx 订单相关的错误

    // 5xxxx 支付相关的错误
    ;


    private String code;
    private String desc;
}
