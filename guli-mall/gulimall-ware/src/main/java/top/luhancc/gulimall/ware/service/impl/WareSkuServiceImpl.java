package top.luhancc.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.luhancc.common.to.mq.StockDetailTo;
import top.luhancc.common.to.mq.StockLockedTo;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;
import top.luhancc.gulimall.ware.dao.WareSkuDao;
import top.luhancc.gulimall.ware.domain.vo.OrderItemVo;
import top.luhancc.gulimall.ware.domain.vo.WareSkuLockVo;
import top.luhancc.gulimall.ware.entity.WareOrderTaskDetailEntity;
import top.luhancc.gulimall.ware.entity.WareOrderTaskEntity;
import top.luhancc.gulimall.ware.entity.WareSkuEntity;
import top.luhancc.gulimall.ware.exceptions.NoStockException;
import top.luhancc.gulimall.ware.feign.OrderFeign;
import top.luhancc.gulimall.ware.service.WareOrderTaskDetailService;
import top.luhancc.gulimall.ware.service.WareOrderTaskService;
import top.luhancc.gulimall.ware.service.WareSkuService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Override
    public PageUtils<WareSkuEntity> queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils<>(page);
    }

    @Override
    @Transactional(rollbackFor = NoStockException.class)
    public Boolean orderLockSku(WareSkuLockVo wareSkuLockVo) {
        //TODO 按照订单的地址的找到一个就近的仓库，锁定库存 暂时不做

        // 保存库存锁定工作单的记录,方便进行追溯
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(taskEntity);

        List<OrderItemVo> locks = wareSkuLockVo.getLocks();
        List<SkuWareHasStock> skuWareHasStockList = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(item.getSkuId());
            List<Long> wareId = baseMapper.listWareIdHasSkuStock(item.getSkuId());
            stock.setWareId(wareId);
            stock.setNum(item.getCount());
            return stock;
        }).collect(Collectors.toList());
        // 2. 锁定库存
        for (SkuWareHasStock hasStock : skuWareHasStockList) {
            Boolean skuLocked = false;
            Long skuId = hasStock.getSkuId();
            Integer lockNum = hasStock.getNum();
            List<Long> wareIdList = hasStock.getWareId();
            if (CollectionUtils.isEmpty(wareIdList)) {
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIdList) {
                int rows = baseMapper.lockSkuStock(wareId, skuId, lockNum);
                if (rows <= 0) {
                    // 当前仓库锁定失败,尝试下一个仓库
                } else {
                    skuLocked = true;
                    // 保存库存锁定成功的工作单,方便追溯和解锁库存
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
                    taskDetailEntity.setSkuId(skuId);
                    taskDetailEntity.setSkuNum(lockNum);
                    taskDetailEntity.setTaskId(taskEntity.getId());
                    taskDetailEntity.setWareId(wareId);
                    taskDetailEntity.setLockStatus(1);
                    wareOrderTaskDetailService.save(taskDetailEntity);
                    // 发送消息给MQ锁定成功
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, stockDetailTo);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    stockLockedTo.setStockDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                    break;// 跳出循环,下一个仓库没必要锁了
                }
            }
            if (!skuLocked) {
                throw new NoStockException(skuId);
            }
        }
        return true;
    }


    /**
     * 解锁库存
     *
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    @Override
    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        baseMapper.unLockStock(skuId, wareId, num);
        // 设置解锁状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);
    }

    @Data
    public static class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }
}