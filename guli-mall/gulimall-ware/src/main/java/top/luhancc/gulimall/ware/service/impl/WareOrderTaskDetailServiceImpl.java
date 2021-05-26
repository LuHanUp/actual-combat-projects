package top.luhancc.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.ware.dao.WareOrderTaskDetailDao;
import top.luhancc.gulimall.ware.entity.WareOrderTaskDetailEntity;
import top.luhancc.gulimall.ware.service.WareOrderTaskDetailService;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils<WareOrderTaskDetailEntity> queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils<>(page);
    }

    @Override
    public List<WareOrderTaskDetailEntity> getByTaskIdAndNotLock(Long taskId) {
        LambdaQueryWrapper<WareOrderTaskDetailEntity> queryWrapper = Wrappers.lambdaQuery(WareOrderTaskDetailEntity.class)
                .eq(WareOrderTaskDetailEntity::getTaskId, taskId)
                .eq(WareOrderTaskDetailEntity::getLockStatus, 1);
        return this.list(queryWrapper);
    }

}