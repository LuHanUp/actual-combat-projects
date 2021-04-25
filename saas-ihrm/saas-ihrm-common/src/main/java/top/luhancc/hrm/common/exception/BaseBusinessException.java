package top.luhancc.hrm.common.exception;

import lombok.Getter;
import top.luhancc.hrm.common.domain.ResultCode;

/**
 * 基础的业务异常类
 *
 * @author luHan
 * @create 2021/4/25 10:05
 * @since 1.0.0
 */
@Getter
public class BaseBusinessException extends RuntimeException {
    private ResultCode resultCode;

    public BaseBusinessException(ResultCode resultCode) {
        super(resultCode.message());
        this.resultCode = resultCode;
    }
}
