package top.luhancc.common.utils;

/**
 * @author luHan
 * @create 2020/12/8 16:55
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConvertFunction<S, T> {
    void custom(S s, T t);
}