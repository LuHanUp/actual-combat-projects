package top.luhancc.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象转换工具类
 *
 * @author luHan
 * @create 2020/12/8 16:54
 * @since 1.0.0
 */
public class PojoUtils {
    public PojoUtils() {
    }

    public static <T, S> T convert(S source, final Class<T> targetClass) {
        return convert(source, targetClass, null);
    }

    public static <T, S> T convert(S source, final Class<T> targetClass, ConvertFunction<S, T> convertFunction) {
        T instance = null;
        if (source != null) {
            instance = BeanUtils.instantiateClass(targetClass);
            BeanUtils.copyProperties(source, instance);
            if (convertFunction != null) {
                convertFunction.custom(source, instance);
            }
        }

        return instance;
    }

    public static <T, S> List<T> convertList(List<S> sourceList, final Class<T> targetClass, final ConvertFunction<S, T> convertFunction) {
        return (List) (sourceList != null && sourceList.size() != 0 ? (List) sourceList.stream().map((s) -> {
            return convert(s, targetClass, convertFunction);
        }).collect(Collectors.toList()) : new ArrayList());
    }

    public static <T, S> List<T> convertList(List<S> sourceList, final Class<T> targetClass) {
        return convertList(sourceList, targetClass, null);
    }

    public static <T, S> PageUtils<T> convertPage(PageUtils<S> pageResult, final Class<T> targetClass) {
        return convertPage(pageResult, targetClass, null);
    }

    public static <T, S> PageUtils<T> convertPage(PageUtils<S> pageResult, final Class<T> targetClass, final ConvertFunction<S, T> convertFunction) {
        return pageResult != null ? PageUtils.of(convertList(pageResult.getList(), targetClass, convertFunction), pageResult) : null;
    }

//    public static <T, S> PageUtils<T> convertPageResult(PageUtils<S> pageResult, final Class<T> targetClass, final PageQuery pageQuery, final ConvertFunction<S, T> convertFunction) {
//        return pageResult != null ? PageResult.of(convertList(pageResult.getData(), targetClass, convertFunction), pageQuery, pageResult.getTotal()) : null;
//    }
//
//    public static <T, S> PageResult<T> convertPageResult(PageResult<S> pageResult, final Class<T> targetClass, final PageQuery pageQuery) {
//        return convertPageResult(pageResult, targetClass, pageQuery, (ConvertFunction) null);
//    }

//    public static <T, S> PageQuery<T> convertPageQuery(PageQuery<S> pageQuery, final Class<T> targetClass, final ConvertFunction<S, T> convertFunction) {
//        if (pageQuery == null) {
//            return null;
//        } else {
//            S querySource = pageQuery.getQuery();
//            T queryTarget = convert(querySource, targetClass, convertFunction);
//            PageQuery<T> pageQueryTarget = (PageQuery) convert(pageQuery, PageQuery.class);
//            pageQueryTarget.setQuery(queryTarget);
//            return pageQueryTarget;
//        }
//    }
//
//    public static <T, S> PageQuery<T> convertPageQuery(PageQuery<S> pageQuery, final Class<T> targetClass) {
//        return convertPageQuery(pageQuery, targetClass, (ConvertFunction) null);
//    }
}
