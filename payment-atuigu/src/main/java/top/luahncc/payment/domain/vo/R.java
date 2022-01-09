package top.luahncc.payment.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.luahncc.payment.domain.exception.BaseException;
import top.luahncc.payment.domain.exception.PaymentException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一结果类
 *
 * @author luHan
 * @create 2022/1/9 14:08
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@SuppressWarnings("unchecked")
public class R<T> implements Serializable {
    private static final long serialVersionUID = -7546765441679416492L;

    private String code;
    private String message;
    private T data;

    public R data(String name, Object value) {
        if (this.data == null) {
            this.data = (T) new HashMap<String, Object>();
        }
        Map<String, Object> data = (Map<String, Object>) this.data;
        data.put(name, value);
        return this;
    }

    /**
     * 成功
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> ok() {
        Map<String, Object> data = new HashMap<>();
        R<Map<String, Object>> r = new R<>();
        r.setCode(PaymentException.SUCCESS.getCode());
        r.setMessage(PaymentException.SUCCESS.getDesc());
        r.setData(data);
        return (R<T>) r;
    }

    /**
     * 成功并设置返回结果数据
     *
     * @param data 结果数据
     * @param <T>
     * @return
     */
    public static <T> R<T> ok(T data) {
        return new R<>("00000", "成功", data);
    }

    /**
     * 失败,注意这个方法不是程序出现error时的方法
     *
     * @param code    失败码
     * @param message 失败消息
     * @param <T>
     * @return
     */
    public static <T> R<T> fail(String code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> fail(BaseException exception) {
        return new R<>(exception.getCode(), exception.getDesc(), null);
    }

    public static <T> R<T> error(Exception exception) {
        log.error("程序发生异常", exception);
        return fail(PaymentException.INTERNAL_ERROR);
    }
}
