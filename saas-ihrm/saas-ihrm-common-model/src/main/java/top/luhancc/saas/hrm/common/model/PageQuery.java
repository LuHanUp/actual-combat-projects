package top.luhancc.saas.hrm.common.model;

import lombok.Data;

/**
 * 分页条件类
 *
 * @author luHan
 * @create 2021/5/13 15:38
 * @since 1.0.0
 */
@Data
public class PageQuery {
    private Integer page = 1;
    private Integer size = 10;
}
