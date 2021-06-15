package top.luhancc.wanxin.finance.transaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.CodePrefixCode;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.StatusCode;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.common.util.CodeNoUtil;
import top.luhancc.wanxin.finance.transaction.common.constant.ProjectCode;
import top.luhancc.wanxin.finance.transaction.common.constant.RepaymentWayCode;
import top.luhancc.wanxin.finance.transaction.feign.ConsumerFeign;
import top.luhancc.wanxin.finance.transaction.mapper.ProjectMapper;
import top.luhancc.wanxin.finance.transaction.mapper.entity.Project;
import top.luhancc.wanxin.finance.transaction.service.ProjectService;

import java.time.LocalDateTime;

/**
 * @author luHan
 * @create 2021/6/15 14:05
 * @since 1.0.0
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    private ConsumerFeign consumerFeign;

    @Override
    public ProjectDTO issueTag(ProjectDTO projectDTO) {
        RestResponse<ConsumerDTO> restResponse = consumerFeign.getCurrConsumer();
        if (restResponse.isSuccessful()) {
            ConsumerDTO consumerDTO = restResponse.getResult();
            projectDTO.setConsumerId(consumerDTO.getId());
            projectDTO.setUserNo(consumerDTO.getUserNo());

            projectDTO.setProjectNo(CodeNoUtil.getNo(CodePrefixCode.CODE_PROJECT_PREFIX));
            projectDTO.setProjectStatus(ProjectCode.COLLECTING.getCode());
            projectDTO.setStatus(StatusCode.STATUS_OUT.getCode());

            projectDTO.setCreateDate(LocalDateTime.now());
            projectDTO.setRepaymentWay(RepaymentWayCode.FIXED_REPAYMENT.getCode());
            projectDTO.setType("NEW");

            Project project = new Project();
            BeanUtils.copyProperties(projectDTO, project);
            this.save(project);
        }
        throw new BusinessException("未登录");
    }
}
