package top.luhancc.wanxin.finance.api.transaction;

import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;

/**
 * @author luHan
 * @create 2021/6/15 13:54
 * @since 1.0.0
 */
public interface TransactionApi {

    /**
     * 借款人发标
     *
     * @param projectDTO
     * @return
     */
    RestResponse<ProjectDTO> issueTag(ProjectDTO projectDTO);
}
