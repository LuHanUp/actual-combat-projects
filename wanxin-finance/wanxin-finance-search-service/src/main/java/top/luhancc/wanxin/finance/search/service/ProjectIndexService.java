package top.luhancc.wanxin.finance.search.service;

import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.search.ProjectQueryParamsDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;

/**
 * @author luHan
 * @create 2021/6/21 10:33
 * @since 1.0.0
 */
public interface ProjectIndexService {

    /**
     * 标的信息快速检索
     *
     * @param projectQueryParamsDTO 检索条件
     * @param pageNo                分页页码
     * @param pageSize              每页数据个数
     * @param sortBy                排序属性
     * @param order                 排序方式
     * @return
     */
    PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO projectQueryParamsDTO,
                                         Integer pageNo, Integer pageSize,
                                         String sortBy, String order);
}
