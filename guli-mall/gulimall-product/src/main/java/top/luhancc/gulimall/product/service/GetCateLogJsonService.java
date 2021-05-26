package top.luhancc.gulimall.product.service;

import top.luhancc.gulimall.product.vo.web.Category2Vo;

import java.util.List;
import java.util.Map;

/**
 * @author luHan
 * @create 2021/1/7 10:07
 * @since 1.0.0
 */
public interface GetCateLogJsonService {
    Map<String, List<Category2Vo>> getCatalogJson();
}
