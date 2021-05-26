package top.luhancc.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.luhancc.common.to.coupon.SeckillOrderTo;
import top.luhancc.common.to.mq.OrderTo;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.order.dao.OrderDao;
import top.luhancc.gulimall.order.domain.cat.CartItem;
import top.luhancc.gulimall.order.domain.order.enume.OrderStatusEnum;
import top.luhancc.gulimall.order.domain.order.to.OrderCreateTo;
import top.luhancc.gulimall.order.domain.order.vo.*;
import top.luhancc.gulimall.order.entity.OrderEntity;
import top.luhancc.gulimall.order.entity.OrderItemEntity;
import top.luhancc.gulimall.order.entity.PaymentInfoEntity;
import top.luhancc.gulimall.order.feign.MemberFeign;
import top.luhancc.gulimall.order.feign.WareFeign;
import top.luhancc.gulimall.order.interceptor.LoginUserInterceptor;
import top.luhancc.gulimall.order.service.OrderItemService;
import top.luhancc.gulimall.order.service.OrderService;
import top.luhancc.gulimall.order.service.PaymentInfoService;
import top.luhancc.gulimall.order.service.cart.CartService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static top.luhancc.gulimall.order.domain.order.vo.OrderConstant.ORDER_CREATE_ORDER_TOPIC;
import static top.luhancc.gulimall.order.domain.order.vo.OrderConstant.ORDER_EVENT_EXCHANGE;


@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private CartService cartService;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private WareFeign wareFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 验证令牌的lua脚本
     */
    private String validatedTokenScript = "if redis.call('get',KEYS[1] == ARGV[1]) then\n" +
            "return redis.call('del',KEYS[1])\n" +
            "else\n" +
            "return 0\n" +
            "end";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirm() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        // 查询
        Long userId = LoginUserInterceptor.loginUser.get().getId();
        List<MemberAddressVo> address = memberFeign.getAddressById(userId);
        // 获取用户购物车中选中的所有商品信息
        List<OrderItemVo> orderItemVos = new ArrayList<>();
        List<CartItem> cartItems = cartService.getUserCartItems(userId);
        BeanUtils.copyProperties(cartItems, orderItemVos);

        orderConfirmVo.setAddress(address);
        orderConfirmVo.setOrderItemVos(orderItemVos);
        orderConfirmVo.setIntegration(LoginUserInterceptor.loginUser.get().getIntegration());

        // 防重复令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setOrderToken(token);
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_KEY + userId, token, 30, TimeUnit.MINUTES);
        return orderConfirmVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitOrderResultVo submitOrder(OrderSubmitVo orderSubmitVo) {
        Long userId = LoginUserInterceptor.loginUser.get().getId();
        // 1. 验证令牌
        String token = orderSubmitVo.getToken();
        int executeCode = redisTemplate.execute(
                new DefaultRedisScript<Long>(validatedTokenScript),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_KEY + userId),
                Collections.singleton(token).toArray()).intValue();
        if (executeCode == 1) {// 令牌验证成功
            // 2. 创建订单
            OrderCreateTo order = createOrder(orderSubmitVo, userId);
            // 3. 验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {// 验价成功
                // 4. 保存订单数据
                saveOrder(order);
                // 5. 锁定库存
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getItems().stream().map(orderItemEntity -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(orderItemEntity.getSkuId());
                    orderItemVo.setCount(orderItemEntity.getSkuQuantity());
                    orderItemVo.setTitle(orderItemEntity.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLocks(locks);
                R r = wareFeign.orderLockSku(wareSkuLockVo);
                if (r.isSuccess()) {
                    // 锁定库存成功
                    rabbitTemplate.convertAndSend(ORDER_EVENT_EXCHANGE, ORDER_CREATE_ORDER_TOPIC, order.getOrder());
                    return SubmitOrderResultVo.builder().code(0).order(order.getOrder()).build();
                } else {
                    // 锁定库存失败
                    return SubmitOrderResultVo.builder().code(4003).build();
                }
            } else {
                // 验价失败
                return SubmitOrderResultVo.builder().code(4002).build();
            }
        } else {
            // 令牌验证失败
            return SubmitOrderResultVo.builder().code(4001).build();
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        LambdaQueryWrapper<OrderEntity> queryWrapper = Wrappers.lambdaQuery(OrderEntity.class)
                .eq(OrderEntity::getOrderSn, orderSn);
        return this.getOne(queryWrapper);
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // 查询订单的最新状态
        OrderEntity newOrderEntity = this.getById(orderEntity.getId());
        if (newOrderEntity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            // 关闭订单
            newOrderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(newOrderEntity);
            // 发送消息到mq告诉库存订单关闭了
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(newOrderEntity, orderTo);
            try {
                //TODO 保证消息一定能发送出去，每条消息状态做好数据库记录 然后定期进行扫描重新发送
                rabbitTemplate.convertAndSend(ORDER_EVENT_EXCHANGE, "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO 将没发送成功的消息进行重试
            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
//        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);
//        List<OrderItemEntity> orderItemEntities = orderItemService.getByOrderSn(orderSn);
//        PayVo payVo = new PayVo();
//        payVo.setOut_trade_no(orderSn);
//        payVo.setSubject(orderItemEntities.get(0).getSkuName());
//        payVo.setTotal_amount(orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString());
//        payVo.setBody(orderItemEntities.get(0).getSkuAttrsVals());

        // 以下是没有数据时的测试支付
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        payVo.setSubject("支付测试");
        payVo.setTotal_amount("0.01");
        payVo.setBody("这是一个描述啊");
        return payVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handlePayResult(PayAsyncVo payAsyncVo) {
        // 1. 保存交易流水
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setTotalAmount(new BigDecimal(payAsyncVo.getTotal_amount()).setScale(2, BigDecimal.ROUND_UP));
        paymentInfoEntity.setSubject(payAsyncVo.getSubject());
        paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfoEntity.setCreateTime(new Date());
        paymentInfoEntity.setCallbackContent(payAsyncVo.getBody());
        Date time = null;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(payAsyncVo.getNotify_time());
        } catch (ParseException e) {
            log.error("转换时间异常", e);
        }
        paymentInfoEntity.setCallbackTime(time);
        paymentInfoService.save(paymentInfoEntity);

        // 2. 修改交易状态
        String trade_status = payAsyncVo.getTrade_status();
        if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
            String orderSn = payAsyncVo.getOut_trade_no();
            baseMapper.updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
        //TODO 保存订单信息,完善下保存的数据
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderEntity.setMemberId(seckillOrderTo.getMemberId());
        orderEntity.setCreateTime(new Date());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setPayAmount(seckillOrderTo.getSeckillPrice().multiply(new BigDecimal(seckillOrderTo.getNum())));
        this.save(orderEntity);

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderItemEntity.setSkuId(seckillOrderTo.getSkuId());
        orderItemEntity.setSkuQuantity(seckillOrderTo.getNum());
        orderItemEntity.setRealAmount(orderEntity.getPayAmount());
        orderItemService.save(orderItemEntity);

        //TODO 扣减库存信息参考createOrder方法
    }

    /**
     * 保存订单数据
     *
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        List<OrderItemEntity> orderItems = order.getItems();

        this.save(orderEntity);
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 创建订单
     *
     * @param orderSubmitVo 订单提交数据
     * @param userId        用户id
     * @return
     */
    private OrderCreateTo createOrder(OrderSubmitVo orderSubmitVo, Long userId) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // ====================订单信息=============================
        OrderEntity orderEntity = new OrderEntity();
        // 生成订单号
        String orderId = IdWorker.getTimeId();
        orderEntity.setOrderSn(orderId);
        // 设置用户信息
        orderEntity.setMemberId(userId);
        orderEntity.setMemberUsername(LoginUserInterceptor.loginUser.get().getUsername());
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());
        // 设置收货信息
        R r = wareFeign.info(orderSubmitVo.getAddrId());
        MemberAddressVo addressVo = r.get("wareInfo", MemberAddressVo.class);
        orderEntity.setReceiverName(addressVo.getName());
        orderEntity.setReceiverPhone(addressVo.getPhone());
        orderEntity.setReceiverPostCode(addressVo.getPostCode());
        orderEntity.setReceiverProvince(addressVo.getProvince());
        orderEntity.setReceiverCity(addressVo.getCity());
        orderEntity.setReceiverRegion(addressVo.getRegion());
        orderEntity.setReceiverDetailAddress(addressVo.getDetailAddress());
        // 设置运费
        R fare = wareFeign.getFare(orderSubmitVo.getAddrId());
        BigDecimal farePrice = fare.get(BigDecimal.class);
        orderEntity.setFreightAmount(farePrice);
        orderCreateTo.setFare(farePrice);

        orderEntity.setPayType(1);
        orderEntity.setSourceType(0);
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(0);
        orderEntity.setDeleteStatus(0);
        orderEntity.setAutoConfirmDay(7);

        orderCreateTo.setOrder(orderEntity);
        // ===================设置订单项==========================
        List<CartItem> itemList = cartService.getUserCartItems(userId);
        if (!CollectionUtils.isEmpty(itemList)) {
            List<OrderItemEntity> itemEntityList = itemList.stream().map(cartItem -> buildOrderItem(cartItem, orderId)).collect(Collectors.toList());
            orderCreateTo.setItems(itemEntityList);

            // 设置积分和成长值
            orderEntity.setIntegration(itemEntityList.stream().mapToInt(OrderItemEntity::getGiftIntegration).sum());
            orderEntity.setGrowth(itemEntityList.stream().mapToInt(OrderItemEntity::getGiftGrowth).sum());

            // ======================设置价格=================================
            BigDecimal totalPrice = new BigDecimal(0);
            BigDecimal couponPrice = new BigDecimal(0);
            BigDecimal promotionPrice = new BigDecimal(0);
            BigDecimal integrationPrice = new BigDecimal(0);
            for (OrderItemEntity orderItemEntity : itemEntityList) {
                totalPrice = totalPrice.add(orderItemEntity.getRealAmount());
                couponPrice = couponPrice.add(orderItemEntity.getCouponAmount());
                promotionPrice = promotionPrice.add(orderItemEntity.getPromotionAmount());
                integrationPrice = integrationPrice.add(orderItemEntity.getIntegrationAmount());
            }
            orderEntity.setTotalAmount(totalPrice);
            orderEntity.setPayAmount(totalPrice.add(farePrice));
            orderCreateTo.setPayPrice(orderEntity.getPayAmount());
            orderEntity.setPromotionAmount(promotionPrice);
            orderEntity.setCouponAmount(couponPrice);
            orderEntity.setIntegrationAmount(integrationPrice);
        }
        return orderCreateTo;
    }

    /**
     * 构建订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(CartItem cartItem, String orderId) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(orderId);
        //TODO 远程获取spu信息
//        orderItemEntity.setSpuId();
//        orderItemEntity.setSpuName();
//        orderItemEntity.setSpuPic();
//        orderItemEntity.setSpuBrand();
//        orderItemEntity.setCategoryId();

        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getImage());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        orderItemEntity.setSkuQuantity(cartItem.getCount());
        orderItemEntity.setSkuAttrsVals(String.join(";", cartItem.getAttr()));
        orderItemEntity.setGiftIntegration(cartItem.getPrice().intValue());
        orderItemEntity.setGiftGrowth(cartItem.getPrice().intValue());

        // 订单项的价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal(0));
        orderItemEntity.setCouponAmount(new BigDecimal(0));
        orderItemEntity.setIntegrationAmount(new BigDecimal(0));
        // 商品的原始价格
        BigDecimal originPrice = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        // 减去优惠价格的应付价格
        BigDecimal realPrice = originPrice.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);
        return orderItemEntity;
    }
}