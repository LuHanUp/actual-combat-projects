package top.luhancc.hrm.common.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用的返回结果类
 *
 * @author luHan
 */
@Data
public class Result<T> {

    private boolean success;//是否成功
    private Integer code;// 返回码
    private String message;//返回信息
    private T data;// 返回数据

    public Result(ResultCode code) {
        this.success = code.success;
        this.code = code.code;
        this.message = code.message;
    }

    public Result(ResultCode code, T data) {
        this.success = code.success;
        this.code = code.code;
        this.message = code.message;
        this.data = data;
    }

    public Result(Integer code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> error() {
        return error(ResultCode.SERVER_ERROR.message);
    }

    public static <T> Result<T> error(String message) {
        return error(ResultCode.SERVER_ERROR.code, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, false);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAIL);
    }
}
