package top.luhancc.gulimall.ware.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.luhancc.common.to.mq.OrderTo;
import top.luhancc.common.to.mq.StockDetailTo;
import top.luhancc.common.to.mq.StockLockedTo;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.ware.domain.vo.OrderVo;
import top.luhancc.gulimall.ware.entity.WareOrderTaskDetailEntity;
import top.luhancc.gulimall.ware.entity.WareOrderTaskEntity;
import top.luhancc.gulimall.ware.feign.OrderFeign;
import top.luhancc.gulimall.ware.service.WareOrderTaskDetailService;
import top.luhancc.gulimall.ware.service.WareOrderTaskService;
import top.luhancc.gulimall.ware.service.WareSkuService;

import java.io.IOException;
import java.util.List;

/**
 * @author luHan
 * @create 2021/1/19 15:16
 * @since 1.0.0
 */
@Component
@RabbitListener(queues = "stock.release.queue")
@Slf4j
public class UnLockStockListener {
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareSkuService wareSkuService;
    @Autowired
    private OrderFeign orderFeign;


    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo stockLockedTo, Channel channel, Message message) throws IOException {
        log.info("ware服务收到解锁库存消息:[{}]", stockLockedTo);
        StockDetailTo stockDetail = stockLockedTo.getStockDetail();
        Long detailId = stockDetail.getId();
        /*
            解锁
            1. 如果库存失败导致数据库回滚了，这种情况无需解锁，就是detailId在数据库中没有这条记录
            2. 如果有detailId这条纪录，那么就需要进行解锁
         */
        WareOrderTaskDetailEntity taskDetailEntity = wareOrderTaskDetailService.getById(detailId);
        if (taskDetailEntity != null) {
            /*
                解锁库存
                1. 没有这个订单，必须解锁
                2. 有这个订单，但是订单状态是取消，需要解锁库存
             */
            Long taskId = stockLockedTo.getId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(taskId);
            String orderSn = taskEntity.getOrderSn();
            // 根据订单号查询订单状态
            R r = orderFeign.getOrderStatus(orderSn);
            if (r.isSuccess()) {
                OrderVo orderVo = r.get(OrderVo.class);
                if (orderVo == null || orderVo.getStatus() == 4) {// 订单已经被取消了
                    // 解锁库存
                    if (taskDetailEntity.getLockStatus() == 1) {// 只有是已经锁定状态的库存才可以解锁
                        wareSkuService.unLockStock(stockDetail.getSkuId(), stockDetail.getWareId(), stockDetail.getSkuNum(), detailId);
                    }
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }
            } else {
                log.error("获取订单状态失败:{}", r);
                // 拒绝消息后将消息重新放入队列，别人的消费者继续消费
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        } else {
            // 无需解锁
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @RabbitHandler
    public void handlerOrderClose(OrderTo orderTo, Channel channel, Message message) throws IOException {
        log.info("ware服务收到订单关闭的消息:[{}]", orderTo);
        String orderSn = orderTo.getOrderSn();
        // 查询最新库存工作单的状态
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long taskId = task.getId();
        // 获取没有解锁的库存工作单的记录
        List<WareOrderTaskDetailEntity> taskDetailEntities = wareOrderTaskDetailService.getByTaskIdAndNotLock(taskId);
        for (WareOrderTaskDetailEntity taskDetailEntity : taskDetailEntities) {
            wareSkuService.unLockStock(taskDetailEntity.getSkuId(), taskDetailEntity.getWareId(), taskDetailEntity.getSkuNum(), taskDetailEntity.getId());
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
