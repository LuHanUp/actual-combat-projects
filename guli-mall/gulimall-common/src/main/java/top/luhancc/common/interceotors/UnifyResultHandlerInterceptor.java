package top.luhancc.common.interceotors;

import com.alibaba.fastjson.JSON;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.luhancc.common.utils.R;

/**
 * 统一返回结果处理器
 *
 * @author luHan
 * @create 2021/1/26 15:33
 * @since 1.0.0
 */
@RestControllerAdvice
public class UnifyResultHandlerInterceptor implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        response.getHeaders().set("Content-Type", "application/json;charset=utf-8");
        R r;
        if (body instanceof R) {
            r = (R) body;
        } else if (selectedConverterType.equals(StringHttpMessageConverter.class)) {
            return JSON.toJSONString(R.ok(body));
        } else {
            r = R.ok(body);
        }
        return r;
    }
}

