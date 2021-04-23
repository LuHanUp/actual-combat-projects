package top.luhancc.hrm.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 分页结果类
 *
 * @param <T>
 * @author luHan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T> {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> rows;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 总页数
     */
    private Integer pageCount;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页显示条数
     */
    private Integer size;
}
