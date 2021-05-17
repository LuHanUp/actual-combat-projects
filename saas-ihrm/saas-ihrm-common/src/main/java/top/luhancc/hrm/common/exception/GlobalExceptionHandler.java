package top.luhancc.hrm.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;

/**
 * 全局异常处理器
 *
 * @author luHan
 * @create 2021/4/25 09:53
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<Void> argumentExceptionHandle(IllegalArgumentException argumentException) {
        log.error("参数异常:{}", argumentException.getLocalizedMessage());
        return Result.error(ResultCode.ARGUMENT_ERROR, argumentException.getLocalizedMessage());
    }

    @ExceptionHandler(value = BaseBusinessException.class)
    public Result<Void> baseBusinessExceptionHandle(BaseBusinessException baseBusinessException) {
        log.error("业务异常:", baseBusinessException);
        return Result.error(baseBusinessException.getResultCode(), baseBusinessException.getLocalizedMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result<Void> exceptionHandle(Exception e) {
        log.error("程序出错了:", e);
        return Result.error();
    }

    @ExceptionHandler(value = Throwable.class)
    public Result<Void> throwableHandle(Throwable e) {
        log.error("程序出错了:", e);
        return Result.error();
    }
}
