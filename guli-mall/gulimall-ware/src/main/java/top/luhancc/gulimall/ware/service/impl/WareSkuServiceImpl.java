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
        //TODO ?????????????????????????????????????????????????????????????????? ????????????

        // ????????????????????????????????????,??????????????????
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
        // 2. ????????????
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
                    // ????????????????????????,?????????????????????
                } else {
                    skuLocked = true;
                    // ????????????????????????????????????,???????????????????????????
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
                    taskDetailEntity.setSkuId(skuId);
                    taskDetailEntity.setSkuNum(lockNum);
                    taskDetailEntity.setTaskId(taskEntity.getId());
                    taskDetailEntity.setWareId(wareId);
                    taskDetailEntity.setLockStatus(1);
                    wareOrderTaskDetailService.save(taskDetailEntity);
                    // ???????????????MQ????????????
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, stockDetailTo);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(taskEntity.getId());
                    stockLockedTo.setStockDetail(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                    break;// ????????????,??????????????????????????????
                }
            }
            if (!skuLocked) {
                throw new NoStockException(skuId);
            }
        }
        return true;
    }


    /**
     * ????????????
     *
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    @Override
    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        baseMapper.unLockStock(skuId, wareId, num);
        // ??????????????????
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