package top.luhancc.common.interceotors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.luhancc.common.utils.R;

/**
 * @author luHan
 * @create 2021/1/26 15:46
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class UnifyErrorHandlerInterceptor {

    @ExceptionHandler(value = Exception.class)
    public R exception(Exception e) {
        String message = e.getMessage();
        log.error("", e);
        return R.error(500, message);
    }

    @ExceptionHandler(value = Throwable.class)
    public R throwable(Throwable e) {
        String message = e.getMessage();
        log.error("", e);
        return R.error(500, message);
    }
}
