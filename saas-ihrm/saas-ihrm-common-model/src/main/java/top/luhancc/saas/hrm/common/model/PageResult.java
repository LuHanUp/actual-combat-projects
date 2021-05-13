package top.luhancc.saas.hrm.common.model;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页结果
 *
 * @author luHan
 * @create 2021/5/13 16:37
 * @since 1.0.0
 */
@Data
public class PageResult<T> {
    private Long total;
    private List<T> rows;
    private Boolean hasNextPage;
    private Integer totalPage;
    private Integer currPage;

    public PageResult(Page<T> page) {
        this.total = page.getTotalElements();
        this.rows = page.getContent();
        this.totalPage = page.getTotalPages();
        this.currPage = page.getPageable().getPageNumber() + 1;
        this.hasNextPage = page.hasNext();
    }
}
