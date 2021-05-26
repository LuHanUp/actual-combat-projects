package top.luhancc.gulimall.search.service;

import top.luhancc.gulimall.search.domain.vo.web.SearchParam;
import top.luhancc.gulimall.search.domain.vo.web.SearchResult;

/**
 * @author luHan
 * @create 2021/1/7 17:31
 * @since 1.0.0
 */
public interface SearchService {
    /**
     * 搜索
     *
     * @param searchParam 搜索条件
     * @return 检索的结果
     */
    SearchResult search(SearchParam searchParam);
}
