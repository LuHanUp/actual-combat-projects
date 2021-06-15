package top.luhancc.wanxin.finance.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;

/**
 * @author luHan
 * @create 2021/6/15 14:05
 * @since 1.0.0
 */
public interface ProjectService extends IService<Project> {

    /**
     * 借款人发标
     *
     * @param projectDTO
     * @return
     */
    ProjectDTO issueTag(ProjectDTO projectDTO);
}
